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
import com.example.luxevista.models.Service;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class AddServiceActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView serviceImageView;
    private EditText nameEditText, categoryEditText, priceEditText, durationEditText, descriptionEditText;
    private CheckBox isAvailableCheckBox;
    private Button saveButton;
    private Uri imageUri;
    private Service existingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        serviceImageView = findViewById(R.id.serviceImageView);
        nameEditText = findViewById(R.id.nameEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        priceEditText = findViewById(R.id.priceEditText);
        durationEditText = findViewById(R.id.durationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        isAvailableCheckBox = findViewById(R.id.isAvailableCheckBox);
        saveButton = findViewById(R.id.saveButton);

        existingService = (Service) getIntent().getSerializableExtra("service");
        if (existingService != null) {
            nameEditText.setText(existingService.getName());
            categoryEditText.setText(existingService.getCategory());
            priceEditText.setText(String.valueOf(existingService.getPrice()));
            durationEditText.setText(String.valueOf(existingService.getDuration()));
            descriptionEditText.setText(existingService.getDescription());
            isAvailableCheckBox.setChecked(existingService.isAvailable());
            if (existingService.getImageUrl() != null && !existingService.getImageUrl().isEmpty()) {
                Glide.with(this)
                    .load(existingService.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(serviceImageView);
            }
        }

        serviceImageView.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveService());
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
            serviceImageView.setImageURI(imageUri);
        }
    }

    private void saveService() {
        String name = nameEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String durationStr = durationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        boolean isAvailable = isAvailableCheckBox.isChecked();

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int duration = Integer.parseInt(durationStr);
        Service service = new Service();
        service.setName(name);
        service.setCategory(category);
        service.setPrice(price);
        service.setDuration(duration);
        service.setDescription(description);
        service.setAvailable(isAvailable);

        if (imageUri != null) {
            uploadImage(service);
        } else if (existingService != null && existingService.getImageUrl() != null) {
            service.setImageUrl(existingService.getImageUrl());
            saveServiceToFirestore(service);
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Service service) {
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
                service.setImageUrl(uploadedUrl);
                runOnUiThread(() -> saveServiceToFirestore(service));
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

    private void saveServiceToFirestore(Service service) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (existingService != null) {
            db.collection("services").document(existingService.getId())
                .set(service)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Service saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save service", Toast.LENGTH_SHORT).show();
                });
        } else {
            db.collection("services")
                .add(service)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add service", Toast.LENGTH_SHORT).show();
                });
        }
    }
} 