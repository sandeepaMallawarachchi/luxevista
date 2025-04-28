package com.example.luxevista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private LinearLayout roomsLayout, servicesLayout, attractionsLayout, bookingsLayout;
    private ImageView backButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        roomsLayout = findViewById(R.id.roomsLayout);
        servicesLayout = findViewById(R.id.servicesLayout);
        attractionsLayout = findViewById(R.id.attractionsLayout);
        bookingsLayout = findViewById(R.id.bookingsLayout);
        backButton = findViewById(R.id.backButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set up click listeners
        roomsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminRoomActivity.class);
            startActivity(intent);
        });

        servicesLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminServiceActivity.class);
            startActivity(intent);
        });

        attractionsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminAttractionActivity.class);
            startActivity(intent);
        });

        bookingsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminBookingActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());

        logoutButton.setOnClickListener(v -> {
            FirebaseManager.getInstance().signOut();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        });
    }
} 