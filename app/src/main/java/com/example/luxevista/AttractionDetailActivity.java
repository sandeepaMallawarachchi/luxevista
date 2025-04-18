package com.example.luxevista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luxevista.models.Attraction;

public class AttractionDetailActivity extends AppCompatActivity {

    private ImageView attractionImageView, backButton;
    private TextView attractionNameTextView, attractionCategoryTextView, attractionDistanceTextView;
    private TextView attractionDescriptionTextView;
    private Button viewOnMapButton;
    
    private Attraction attraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        // Get attraction from intent
        attraction = (Attraction) getIntent().getSerializableExtra("attraction");
        if (attraction == null) {
            Toast.makeText(this, "Error loading attraction details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI elements
        attractionImageView = findViewById(R.id.attractionImageView);
        backButton = findViewById(R.id.backButton);
        attractionNameTextView = findViewById(R.id.attractionNameTextView);
        attractionCategoryTextView = findViewById(R.id.attractionCategoryTextView);
        attractionDistanceTextView = findViewById(R.id.attractionDistanceTextView);
        attractionDescriptionTextView = findViewById(R.id.attractionDescriptionTextView);
        viewOnMapButton = findViewById(R.id.viewOnMapButton);

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load attraction details
        loadAttractionDetails();

        // Set up view on map button
        viewOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is a placeholder - in a real app, you would use actual coordinates
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(attraction.getName()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(Intent.createChooser(mapIntent, "Open with"));
            }
        });
    }

    private void loadAttractionDetails() {
        attractionNameTextView.setText(attraction.getName());
        attractionCategoryTextView.setText(attraction.getCategory());
        attractionDistanceTextView.setText(attraction.getDistance() + " km away");
        attractionDescriptionTextView.setText(attraction.getDescription());

        // Load image using Glide
        if (attraction.getImageUrl() != null && !attraction.getImageUrl().isEmpty()) {
            Glide.with(this)
                .load(attraction.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
                .into(attractionImageView);
        } else {
            attractionImageView.setImageResource(R.drawable.placeholder_image);
        }
    }
}
