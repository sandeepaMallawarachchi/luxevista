package com.example.luxevista.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
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

import java.util.*;

public class HomeFragment extends Fragment {

    private RecyclerView roomsRecyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList = new ArrayList<>();
    private List<Room> filteredList = new ArrayList<>();

    private FirebaseFirestore firestore;

    private EditText searchBar;
    private Spinner typeSpinner;
    private SeekBar priceSeekBar;
    private TextView priceLabel;

    private String selectedType = "All";
    private int selectedMaxPrice = 100000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        roomsRecyclerView = view.findViewById(R.id.roomsRecyclerView);
        searchBar = view.findViewById(R.id.searchEditText);
        typeSpinner = view.findViewById(R.id.typeSpinner);
        priceSeekBar = view.findViewById(R.id.priceSeekBar);
        priceLabel = view.findViewById(R.id.priceTextView);

        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        roomAdapter = new RoomAdapter(getContext(), filteredList);
        roomsRecyclerView.setAdapter(roomAdapter);

        firestore = FirebaseFirestore.getInstance();
        setupFilters();
        loadRoomsFromFirestore();

        return view;
    }

    private void setupFilters() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.room_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = parent.getItemAtPosition(position).toString();
                filterRooms();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        priceSeekBar.setMax(100000);
        priceSeekBar.setProgress(100000);
        priceLabel.setText("Max Price: Any");

        priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedMaxPrice = progress;
                priceLabel.setText(progress == 100000 ? "Max Price: Any" : "Max Price: LKR" + progress);
                filterRooms();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRooms();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadRoomsFromFirestore() {
        firestore.collection("rooms").get().addOnSuccessListener(queryDocumentSnapshots -> {
            roomList.clear();
            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                Room room = snapshot.toObject(Room.class);
                room.setId(snapshot.getId());
                roomList.add(room);
            }
            filterRooms();
        });
    }

    private void filterRooms() {
        String query = searchBar.getText().toString().toLowerCase();
        filteredList.clear();

        for (Room room : roomList) {
            boolean matchesType = selectedType.equals("All") || room.getType().equalsIgnoreCase(selectedType);
            boolean matchesPrice = selectedMaxPrice == 100000 || room.getPricePerNight() <= selectedMaxPrice;
            boolean matchesSearch = room.getName().toLowerCase().contains(query);

            if (matchesType && matchesPrice && matchesSearch) {
                filteredList.add(room);
            }
        }

        roomAdapter.notifyDataSetChanged();
    }
}
