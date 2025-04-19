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
import com.example.luxevista.adapters.ServiceAdapter;
import com.example.luxevista.models.Service;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment {

    private RecyclerView servicesRecyclerView;
    private ServiceAdapter serviceAdapter;
    private List<Service> serviceList;
    private DatabaseReference servicesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        // Initialize RecyclerView
        servicesRecyclerView = view.findViewById(R.id.servicesRecyclerView);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize service list and adapter
        serviceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(getContext(), serviceList);
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Initialize Firebase
        servicesRef = FirebaseDatabase.getInstance().getReference().child("Services");
        
        // Load services from Firebase
        loadServices();

        return view;
    }

    private void loadServices() {
        FirebaseFirestore.getInstance()
                .collection("services")
                .get()
                .addOnSuccessListener(query -> {
                    serviceList.clear();
                    for (DocumentSnapshot snapshot : query.getDocuments()) {
                        Service service = snapshot.toObject(Service.class);
                        if (service != null) {
                            service.setId(snapshot.getId());
                            serviceList.add(service);
                        }
                    }
                    serviceAdapter.notifyDataSetChanged();
                });
    }
}
