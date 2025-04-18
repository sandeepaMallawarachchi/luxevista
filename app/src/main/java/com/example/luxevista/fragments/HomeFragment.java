package com.example.luxevista.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevista.R;
import com.example.luxevista.adapters.RoomAdapter;
import com.example.luxevista.models.Room;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView roomsRecyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView
        roomsRecyclerView = view.findViewById(R.id.roomsRecyclerView);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firestore and adapter
        firestore = FirebaseFirestore.getInstance();
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(getContext(), roomList);
        roomsRecyclerView.setAdapter(roomAdapter);

        // Load rooms from Firestore
        loadRoomsFromFirestore();

        return view;
    }

    private void loadRoomsFromFirestore() {
        firestore.collection("rooms")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    roomList.clear();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Room room = snapshot.toObject(Room.class);
                        if (room != null) {
                            room.setId(snapshot.getId());
                            roomList.add(room);
                        }
                        Log.d("FirestoreDebug", "Room loaded with ID: " + snapshot.getId());
                    }
                    roomAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

}
