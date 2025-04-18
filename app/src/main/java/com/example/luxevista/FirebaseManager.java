package com.example.luxevista;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }


    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public String getCurrentUserId() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    public DatabaseReference getUsersReference() {
        return mDatabase.child("Users");
    }

    public DatabaseReference getRoomsReference() {
        return mDatabase.child("Rooms");
    }

    public DatabaseReference getServicesReference() {
        return mDatabase.child("Services");
    }

    public DatabaseReference getBookingsReference() {
        return mDatabase.child("Bookings");
    }

    public DatabaseReference getAttractionsReference() {
        return mDatabase.child("Attractions");
    }

    public void signOut() {
        mAuth.signOut();
    }
}
