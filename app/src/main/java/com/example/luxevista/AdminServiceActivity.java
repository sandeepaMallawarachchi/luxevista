package com.example.luxevista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevista.adapters.AdminServiceAdapter;
import com.example.luxevista.models.Service;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminServiceActivity extends AppCompatActivity {
    private RecyclerView servicesRecyclerView;
    private AdminServiceAdapter serviceAdapter;
    private List<Service> serviceList;
    private ImageView backButton;
    private FloatingActionButton addServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service);

        // servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        // backButton = findViewById(R.id.backButton);
        // addServiceButton = findViewById(R.id.addServiceButton);

        serviceList = new ArrayList<>();
        serviceAdapter = new AdminServiceAdapter(serviceList, this);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        servicesRecyclerView.setAdapter(serviceAdapter);

        loadServices();

        backButton.setOnClickListener(v -> finish());
        addServiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminServiceActivity.this, AddServiceActivity.class);
            startActivity(intent);
        });
    }

    private void loadServices() {
        FirebaseFirestore.getInstance()
            .collection("services")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                serviceList.clear();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Service service = snapshot.toObject(Service.class);
                    service.setId(snapshot.getId());
                    serviceList.add(service);
                }
                serviceAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(AdminServiceActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show();
            });
    }
} 