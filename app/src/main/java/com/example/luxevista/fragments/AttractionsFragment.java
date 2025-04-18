package com.example.luxevista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevista.R;
import com.example.luxevista.adapters.AttractionAdapter;
import com.example.luxevista.models.Attraction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AttractionsFragment extends Fragment {

    private RecyclerView attractionsRecyclerView;
    private AttractionAdapter attractionAdapter;
    private List<Attraction> attractionList;
    private DatabaseReference attractionsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attractions, container, false);

        // Initialize RecyclerView
        attractionsRecyclerView = view.findViewById(R.id.attractionsRecyclerView);
        attractionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize attraction list and adapter
        attractionList = new ArrayList<>();
        attractionAdapter = new AttractionAdapter(getContext(), attractionList);
        attractionsRecyclerView.setAdapter(attractionAdapter);

        // Initialize Firebase
        attractionsRef = FirebaseDatabase.getInstance().getReference().child("Attractions");
        
        // Load attractions from Firebase
        loadAttractions();

        return view;
    }

    private void loadAttractions() {
        attractionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attractionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Attraction attraction = snapshot.getValue(Attraction.class);
                    attractionList.add(attraction);
                }
                attractionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
