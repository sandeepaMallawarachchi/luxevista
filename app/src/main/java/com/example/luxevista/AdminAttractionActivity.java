package com.example.luxevista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevista.adapters.AdminAttractionAdapter;
import com.example.luxevista.models.Attraction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminAttractionActivity extends AppCompatActivity {
    private RecyclerView attractionsRecyclerView;
    private AdminAttractionAdapter attractionAdapter;
    private List<Attraction> attractionList;
    private ImageView backButton;
    private FloatingActionButton addAttractionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attraction);

        attractionsRecyclerView = findViewById(R.id.attractionsRecyclerView);
        backButton = findViewById(R.id.backButton);
        addAttractionButton = findViewById(R.id.addAttractionButton);

        attractionList = new ArrayList<>();
        attractionAdapter = new AdminAttractionAdapter(attractionList, this);
        attractionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attractionsRecyclerView.setAdapter(attractionAdapter);

        loadAttractions();

        backButton.setOnClickListener(v -> finish());
        addAttractionButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminAttractionActivity.this, AddAttractionActivity.class);
            startActivity(intent);
        });
    }

    private void loadAttractions() {
        FirebaseFirestore.getInstance()
            .collection("attractions")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                attractionList.clear();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Attraction attraction = snapshot.toObject(Attraction.class);
                    attraction.setId(snapshot.getId());
                    attractionList.add(attraction);
                }
                attractionAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(AdminAttractionActivity.this, "Failed to load attractions", Toast.LENGTH_SHORT).show();
            });
    }
} 