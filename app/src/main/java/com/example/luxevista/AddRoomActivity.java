package com.example.luxevista;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.luxevista.models.Room;
import com.google.firebase.firestore.FirebaseFirestore;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

public class AddRoomActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView roomImageView;
    private EditText nameEditText;
    private EditText typeEditText;
    private EditText priceEditText;
    private EditText descriptionEditText;
    private EditText maxOccupancyEditText;
    private CheckBox hasOceanViewCheckBox;
    private CheckBox isAvailableCheckBox;
    private Button saveButton;
    private Uri imageUri;
    private Room existingRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        // Initialize views
        roomImageView = findViewById(R.id.roomImageView);
        nameEditText = findViewById(R.id.nameEditText);
        typeEditText = findViewById(R.id.typeEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        maxOccupancyEditText = findViewById(R.id.maxOccupancyEditText);
        hasOceanViewCheckBox = findViewById(R.id.hasOceanViewCheckBox);
        isAvailableCheckBox = findViewById(R.id.isAvailableCheckBox);
        saveButton = findViewById(R.id.saveButton);

        // Check if editing existing room
        existingRoom = (Room) getIntent().getSerializableExtra("room");
        if (existingRoom != null) {
            nameEditText.setText(existingRoom.getName());
            typeEditText.setText(existingRoom.getType());
            priceEditText.setText(String.valueOf(existingRoom.getPricePerNight()));
            descriptionEditText.setText(existingRoom.getDescription());
            maxOccupancyEditText.setText(String.valueOf(existingRoom.getMaxOccupancy()));
            hasOceanViewCheckBox.setChecked(existingRoom.isHasOceanView());
            isAvailableCheckBox.setChecked(existingRoom.isAvailable());

            if (existingRoom.getImageUrl() != null && !existingRoom.getImageUrl().isEmpty()) {
                Glide.with(this)
                    .load(existingRoom.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(roomImageView);
            }
        }

        // Set up click listeners
        roomImageView.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveRoom());
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
            roomImageView.setImageURI(imageUri);
        }
    }

    private void saveRoom() {
        String name = nameEditText.getText().toString().trim();
        String type = typeEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String maxOccupancyStr = maxOccupancyEditText.getText().toString().trim();
        boolean hasOceanView = hasOceanViewCheckBox.isChecked();
        boolean isAvailable = isAvailableCheckBox.isChecked();

        if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty() || description.isEmpty() || maxOccupancyStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int maxOccupancy = Integer.parseInt(maxOccupancyStr);
        Room room = new Room();
        room.setName(name);
        room.setType(type);
        room.setPricePerNight(price);
        room.setDescription(description);
        room.setMaxOccupancy(maxOccupancy);
        room.setHasOceanView(hasOceanView);
        room.setAvailable(isAvailable);

        if (imageUri != null) {
            uploadImage(room);
        } else if (existingRoom != null && existingRoom.getImageUrl() != null) {
            room.setImageUrl(existingRoom.getImageUrl());
            saveRoomToFirebase(room);
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Room room) {
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
                room.setImageUrl(uploadedUrl);
                runOnUiThread(() -> saveRoomToFirebase(room));
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

    private void saveRoomToFirebase(Room room) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (existingRoom != null) {
            db.collection("rooms").document(existingRoom.getId())
                .set(room)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Room saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save room", Toast.LENGTH_SHORT).show();
                });
        } else {
            db.collection("rooms")
                .add(room)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Room added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add room", Toast.LENGTH_SHORT).show();
                });
        }
    }
} 