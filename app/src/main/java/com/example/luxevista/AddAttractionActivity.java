package com.example.luxevista;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.luxevista.models.Attraction;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class AddAttractionActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView attractionImageView;
    private EditText nameEditText, categoryEditText, distanceEditText, descriptionEditText;
    private CheckBox isRecommendedCheckBox;
    private Button saveButton;
    private Uri imageUri;
    private Attraction existingAttraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attraction);

        attractionImageView = findViewById(R.id.attractionImageView);
        nameEditText = findViewById(R.id.nameEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        distanceEditText = findViewById(R.id.distanceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        isRecommendedCheckBox = findViewById(R.id.isRecommendedCheckBox);
        saveButton = findViewById(R.id.saveButton);

        existingAttraction = (Attraction) getIntent().getSerializableExtra("attraction");
        if (existingAttraction != null) {
            nameEditText.setText(existingAttraction.getName());
            categoryEditText.setText(existingAttraction.getCategory());
            distanceEditText.setText(String.valueOf(existingAttraction.getDistance()));
            descriptionEditText.setText(existingAttraction.getDescription());
            isRecommendedCheckBox.setChecked(existingAttraction.isRecommended());
            if (existingAttraction.getImageUrl() != null && !existingAttraction.getImageUrl().isEmpty()) {
                Glide.with(this)
                    .load(existingAttraction.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(attractionImageView);
            }
        }

        attractionImageView.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveAttraction());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            attractionImageView.setImageURI(imageUri);
        }
    }

    private void saveAttraction() {
        String name = nameEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String distanceStr = distanceEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        boolean isRecommended = isRecommendedCheckBox.isChecked();

        if (name.isEmpty() || category.isEmpty() || distanceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double distance = Double.parseDouble(distanceStr);
        Attraction attraction = new Attraction();
        attraction.setName(name);
        attraction.setCategory(category);
        attraction.setDistance(distance);
        attraction.setDescription(description);
        attraction.setRecommended(isRecommended);

        if (imageUri != null) {
            uploadImage(attraction);
        } else if (existingAttraction != null && existingAttraction.getImageUrl() != null) {
            attraction.setImageUrl(existingAttraction.getImageUrl());
            saveAttractionToFirestore(attraction);
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Attraction attraction) {
        new Thread(() -> {
            try {
                File tempFile = createTempFileFromUri(imageUri);
                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", "dfytmvzw5",
                        "api_key", "638346467257877",
                        "api_secret", "eZcdojjN6nxFEvHZsJgKXiJhtvY"
                ));
                Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());
                String uploadedUrl = (String) uploadResult.get("secure_url");
                attraction.setImageUrl(uploadedUrl);
                runOnUiThread(() -> saveAttractionToFirestore(attraction));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
        OutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, len);
        outputStream.close();
        inputStream.close();
        return tempFile;
    }

    private void saveAttractionToFirestore(Attraction attraction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (existingAttraction != null) {
            db.collection("attractions").document(existingAttraction.getId())
                .set(attraction)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Attraction saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save attraction", Toast.LENGTH_SHORT).show();
                });
        } else {
            db.collection("attractions")
                .add(attraction)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Attraction added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add attraction", Toast.LENGTH_SHORT).show();
                });
        }
    }
} 