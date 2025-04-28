package com.example.luxevista;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevista.adapters.AdminBookingAdapter;
import com.example.luxevista.models.Booking;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingActivity extends AppCompatActivity {

    private RecyclerView bookingsRecyclerView;
    private AdminBookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking);

        // Initialize views
        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView);
        backButton = findViewById(R.id.backButton);

        // Setup RecyclerView
        bookingList = new ArrayList<>();
        bookingAdapter = new AdminBookingAdapter(bookingList, this);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingsRecyclerView.setAdapter(bookingAdapter);

        // Load bookings from Firebase
        loadBookings();

        // Set up click listeners
        backButton.setOnClickListener(v -> finish());
    }

    private void loadBookings() {
        FirebaseFirestore.getInstance()
                .collection("bookings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookingList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Booking booking = document.toObject(Booking.class);
                        booking.setId(document.getId());
                        bookingList.add(booking);
                    }
                    bookingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                });
    }

    public void updateBookingStatus(String bookingId, String status) {
        FirebaseFirestore.getInstance()
                .collection("bookings")
                .document(bookingId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking " + status + " successfully", Toast.LENGTH_SHORT).show();
                    loadBookings();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update booking status", Toast.LENGTH_SHORT).show();
                });
    }
} 