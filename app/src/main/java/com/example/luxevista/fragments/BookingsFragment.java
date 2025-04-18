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

import com.example.luxevista.FirebaseManager;
import com.example.luxevista.R;
import com.example.luxevista.adapters.BookingAdapter;
import com.example.luxevista.models.Booking;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingsFragment extends Fragment {

    private RecyclerView bookingsRecyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        // Initialize RecyclerView
        bookingsRecyclerView = view.findViewById(R.id.bookingsRecyclerView);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize booking list and adapter
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(getContext(), bookingList);
        bookingsRecyclerView.setAdapter(bookingAdapter);
        
        // Load bookings from Firebase
        loadBookings();

        return view;
    }

    private void loadBookings() {
        FirebaseFirestore.getInstance().collection("bookings")
                .whereEqualTo("userId", FirebaseManager.getInstance().getCurrentUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookingList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        Booking booking = snapshot.toObject(Booking.class);
                        bookingList.add(booking);
                    }
                    bookingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Log or show error
                });
    }
}
