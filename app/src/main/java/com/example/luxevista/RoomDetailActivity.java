package com.example.luxevista;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luxevista.models.Booking;
import com.example.luxevista.models.Room;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RoomDetailActivity extends AppCompatActivity {

    private ImageView roomImageView, backButton;
    private TextView roomNameTextView, roomTypeTextView, roomPriceTextView, roomDescriptionTextView;
    private TextView checkInDateTextView, checkOutDateTextView, totalPriceTextView;
    private Button bookNowButton;

    private Room room;
    private Date checkInDate, checkOutDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    private boolean isRoomAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        // Get room from intent
        room = getIntent().getSerializableExtra("room", Room.class);
        if (room == null) {
            Toast.makeText(this, "Error loading room details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (room.getId() == null || room.getId().isEmpty()) {
            Toast.makeText(this, "Room ID is missing. Cannot proceed.", Toast.LENGTH_LONG).show();
            return;
        }

        // Initialize UI elements
        roomImageView = findViewById(R.id.roomImageView);
        backButton = findViewById(R.id.backButton);
        roomNameTextView = findViewById(R.id.roomNameTextView);
        roomTypeTextView = findViewById(R.id.roomTypeTextView);
        roomPriceTextView = findViewById(R.id.roomPriceTextView);
        roomDescriptionTextView = findViewById(R.id.roomDescriptionTextView);
        checkInDateTextView = findViewById(R.id.checkInDateTextView);
        checkOutDateTextView = findViewById(R.id.checkOutDateTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        bookNowButton = findViewById(R.id.bookNowButton);

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load room details
        loadRoomDetails();

        if (room.getId() != null && !room.getId().isEmpty()) {
            FirebaseManager.getInstance().getRoomsReference()
                    .child(room.getId())
                    .child("isAvailable")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        Boolean availability = snapshot.getValue(Boolean.class);
                        if (availability != null) {
                            isRoomAvailable = availability;
                            room.setAvailable(availability); // Update the Room object
                            if (!isRoomAvailable) {
                                Toast.makeText(this, "This room is already booked.", Toast.LENGTH_SHORT).show();
                                bookNowButton.setEnabled(false);
                            } else {
                                bookNowButton.setEnabled(true);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error checking availability", Toast.LENGTH_SHORT).show();
                        // Assume available if check fails (optional)
                        isRoomAvailable = true;
                        room.setAvailable(true);
                    });
        }else {
            Toast.makeText(this, "Invalid room ID", Toast.LENGTH_SHORT).show();
            bookNowButton.setEnabled(false);
        }

        // Set up date pickers
        checkInDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);
            }
        });

        checkOutDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false);
            }
        });

        // Set up book now button
        bookNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookRoom();
            }
        });
    }

    private void loadRoomDetails() {
        roomNameTextView.setText(room.getName());
        roomTypeTextView.setText(room.getType());
        roomPriceTextView.setText("$" + room.getPricePerNight() + " / night");
        roomDescriptionTextView.setText(room.getDescription());

        // Load image using Glide
        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(this)
                .load(room.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
                .into(roomImageView);
        } else {
            roomImageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void showDatePickerDialog(final boolean isCheckIn) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if (isCheckIn) {
                            checkInDate = calendar.getTime();
                            checkInDateTextView.setText(dateFormat.format(checkInDate));
                        } else {
                            checkOutDate = calendar.getTime();
                            checkOutDateTextView.setText(dateFormat.format(checkOutDate));
                        }

                        updateTotalPrice();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set min date to today for check-in
        if (isCheckIn) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }
        // Set min date to check-in date for check-out
        else if (checkInDate != null) {
            datePickerDialog.getDatePicker().setMinDate(checkInDate.getTime() + TimeUnit.DAYS.toMillis(1));
        } else {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        }

        datePickerDialog.show();
    }

    private void updateTotalPrice() {
        if (checkInDate != null && checkOutDate != null) {
            long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (diffInDays > 0) {
                double totalPrice = diffInDays * room.getPricePerNight();
                totalPriceTextView.setText("LKR" + totalPrice);
                bookNowButton.setEnabled(true);
            } else {
                totalPriceTextView.setText("LKR0");
                bookNowButton.setEnabled(false);
            }
        } else {
            totalPriceTextView.setText("LKR0");
            bookNowButton.setEnabled(false);
        }
    }

    private void bookRoom() {
        if (checkInDate == null || checkOutDate == null) {
            Toast.makeText(this, "Please select check-in and check-out dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!room.isAvailable()) {
            Toast.makeText(this, "This room is no longer available.", Toast.LENGTH_SHORT).show();
            bookNowButton.setEnabled(false);
            return;
        }

        // Calculate total price
        long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        double totalPrice = diffInDays * room.getPricePerNight();

        // Create booking
        String bookingId = UUID.randomUUID().toString();
        String userId = FirebaseManager.getInstance().getCurrentUserId();

        Booking booking = new Booking(
                bookingId,
                userId,
                room.getId(),
                "room",
                room.getName(),
                checkInDate,
                checkOutDate,
                totalPrice,
                "pending"
        );

        // Save booking to Firebase
        FirebaseManager.getInstance().getFirestore()
                .collection("bookings")
                .document(bookingId)
                .set(booking)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(RoomDetailActivity.this, "Booking successful!", Toast.LENGTH_SHORT).show();

                    // Mark room as not available in Firestore
                    FirebaseManager.getInstance().getFirestore()
                            .collection("rooms")
                            .document(room.getId())
                            .update("isAvailable", false);

                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Booking failed: ", e);
                    Toast.makeText(RoomDetailActivity.this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
