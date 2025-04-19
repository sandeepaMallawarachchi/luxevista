package com.example.luxevista.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.luxevista.FirebaseManager;
import com.example.luxevista.LoginActivity;
import com.example.luxevista.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import java.io.*;
import java.util.*;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 100;
    private ImageView profileImageView;
    private TextView usernameTextView, emailTextView, totalTextView, confirmedTextView, cancelledTextView;
    private Button logoutButton;

    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private DocumentReference userDocRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = view.findViewById(R.id.profileImageView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        totalTextView = view.findViewById(R.id.totalBookingsTextView);
        confirmedTextView = view.findViewById(R.id.confirmedBookingsTextView);
        cancelledTextView = view.findViewById(R.id.cancelledBookingsTextView);
        logoutButton = view.findViewById(R.id.logoutButton);

        currentUser = FirebaseManager.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        userDocRef = firestore.collection("users").document(currentUser.getUid());

        loadUserData();
        loadBookingSummary();

        logoutButton.setOnClickListener(v -> {
            FirebaseManager.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        profileImageView.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void loadUserData() {
        emailTextView.setText(currentUser.getEmail());

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                String imageUrl = documentSnapshot.getString("imageUrl");

                usernameTextView.setText(username != null ? username : "Username");

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(requireContext()).load(imageUrl).into(profileImageView);
                }
            }
        });
    }

    private void loadBookingSummary() {
        firestore.collection("bookings")
                .whereEqualTo("userId", currentUser.getUid())
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    int total = 0, confirmed = 0, cancelled = 0;

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        total++;
                        String status = doc.getString("status");
                        if ("confirmed".equalsIgnoreCase(status)) confirmed++;
                        if ("cancelled".equalsIgnoreCase(status)) cancelled++;
                    }

                    totalTextView.setText("Total Bookings: " + total);
                    confirmedTextView.setText("Confirmed: " + confirmed);
                    cancelledTextView.setText("Cancelled: " + cancelled);
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            uploadToCloudinary(data.getData());
        }
    }

    private void uploadToCloudinary(Uri imageUri) {
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

                userDocRef.update("imageUrl", uploadedUrl);
                requireActivity().runOnUiThread(() -> {
                    Glide.with(requireContext()).load(uploadedUrl).into(profileImageView);
                    Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload", ".jpg", requireContext().getCacheDir());
        OutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, len);
        outputStream.close();
        inputStream.close();
        return tempFile;
    }
}