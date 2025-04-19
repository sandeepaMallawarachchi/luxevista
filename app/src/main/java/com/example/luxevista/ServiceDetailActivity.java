package com.example.luxevista;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luxevista.models.Booking;
import com.example.luxevista.models.Service;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ServiceDetailActivity extends AppCompatActivity {

    private ImageView serviceImageView, backButton;
    private TextView serviceNameTextView, serviceCategoryTextView, servicePriceTextView, serviceDescriptionTextView;
    private TextView serviceDateTextView, serviceTimeTextView, totalPriceTextView;
    private Button bookNowButton;
    
    private Service service;
    private Date serviceDate;
    private int serviceHour, serviceMinute;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        // Get service from intent
        service = (Service) getIntent().getSerializableExtra("service");
        if (service == null) {
            Toast.makeText(this, "Error loading service details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI elements
        serviceImageView = findViewById(R.id.serviceImageView);
        backButton = findViewById(R.id.backButton);
        serviceNameTextView = findViewById(R.id.serviceNameTextView);
        serviceCategoryTextView = findViewById(R.id.serviceCategoryTextView);
        servicePriceTextView = findViewById(R.id.servicePriceTextView);
        serviceDescriptionTextView = findViewById(R.id.serviceDescriptionTextView);
        serviceDateTextView = findViewById(R.id.serviceDateTextView);
        serviceTimeTextView = findViewById(R.id.serviceTimeTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        bookNowButton = findViewById(R.id.bookNowButton);

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load service details
        loadServiceDetails();

        // Set up date and time pickers
        serviceDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        serviceTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Set up book now button
        bookNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookService();
            }
        });
        
        // Set initial total price
        totalPriceTextView.setText("LKR " + service.getPrice());
    }

    private void loadServiceDetails() {
        serviceNameTextView.setText(service.getName());
        serviceCategoryTextView.setText(service.getCategory());
        servicePriceTextView.setText("LKR " + service.getPrice());
        serviceDescriptionTextView.setText(service.getDescription());

        // Load image using Glide
        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Glide.with(this)
                .load(service.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
                .into(serviceImageView);
        } else {
            serviceImageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        
                        serviceDate = calendar.getTime();
                        serviceDateTextView.setText(dateFormat.format(serviceDate));
                        updateBookButtonState();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set min date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        serviceHour = hourOfDay;
                        serviceMinute = minute;
                        
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        
                        serviceTimeTextView.setText(timeFormat.format(calendar.getTime()));
                        updateBookButtonState();
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        
        timePickerDialog.show();
    }

    private void updateBookButtonState() {
        bookNowButton.setEnabled(serviceDate != null && serviceTimeTextView.getText().length() > 0);
    }

    private void bookService() {
        if (serviceDate == null || serviceTimeTextView.getText().length() == 0) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set service time
        calendar.setTime(serviceDate);
        calendar.set(Calendar.HOUR_OF_DAY, serviceHour);
        calendar.set(Calendar.MINUTE, serviceMinute);
        Date startDate = calendar.getTime();

        // Calculate end date (start date + duration)
        calendar.add(Calendar.MINUTE, service.getDuration());
        Date endDate = calendar.getTime();

        // Create booking
        String bookingId = UUID.randomUUID().toString();
        String userId = FirebaseManager.getInstance().getCurrentUserId();

        Booking booking = new Booking(
                bookingId,
                userId,
                service.getId(),
                "service",
                service.getName(),
                startDate,
                endDate,
                service.getPrice(),
                "pending"
        );

        // Save booking to Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bookings")
                .document(bookingId)
                .set(booking)
                .addOnSuccessListener(unused -> {
                    // Mark the service as unavailable after successful booking
                    db.collection("services")
                            .document(service.getId())
                            .update("isAvailable", false)
                            .addOnSuccessListener(unused2 -> {
                                Toast.makeText(ServiceDetailActivity.this, "Booking successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ServiceDetailActivity.this, "Service update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ServiceDetailActivity.this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}
