package com.example.luxevista.models;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class Room implements Serializable {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private double pricePerNight;
    private String type;
    private boolean hasOceanView;
    private int maxOccupancy;
    @PropertyName("isAvailable")
    private boolean isAvailable;

    // Required empty constructor for Firebase
    public Room() {
    }

    public Room(String id, String name, String description, String imageUrl, double pricePerNight,
                String type, boolean hasOceanView, int maxOccupancy, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.pricePerNight = pricePerNight;
        this.type = type;
        this.hasOceanView = hasOceanView;
        this.maxOccupancy = maxOccupancy;
        this.isAvailable = isAvailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHasOceanView() {
        return hasOceanView;
    }

    public void setHasOceanView(boolean hasOceanView) {
        this.hasOceanView = hasOceanView;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    @PropertyName("isAvailable")
    public boolean isAvailable() {
        return isAvailable;
    }

    @PropertyName("isAvailable")
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
