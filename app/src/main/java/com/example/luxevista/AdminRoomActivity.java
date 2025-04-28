package com.example.luxevista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevista.adapters.AdminRoomAdapter;
import com.example.luxevista.models.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminRoomActivity extends AppCompatActivity {

    private RecyclerView roomsRecyclerView;
    private AdminRoomAdapter roomAdapter;
    private List<Room> roomList;
    private ImageView backButton;
    private FloatingActionButton addRoomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room);

        // Initialize views
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        backButton = findViewById(R.id.backButton);
        addRoomButton = findViewById(R.id.addRoomButton);

        // Setup RecyclerView
        roomList = new ArrayList<>();
        roomAdapter = new AdminRoomAdapter(roomList, this);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsRecyclerView.setAdapter(roomAdapter);

        // Load rooms from Firebase
        loadRooms();

        // Set up click listeners
        backButton.setOnClickListener(v -> finish());

        addRoomButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminRoomActivity.this, AddRoomActivity.class);
            startActivity(intent);
        });
    }

    private void loadRooms() {
        FirebaseFirestore.getInstance()
            .collection("rooms")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                roomList.clear();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Room room = snapshot.toObject(Room.class);
                    room.setId(snapshot.getId());
                    roomList.add(room);
                }
                roomAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(AdminRoomActivity.this, "Failed to load rooms", Toast.LENGTH_SHORT).show();
            });
    }
} 