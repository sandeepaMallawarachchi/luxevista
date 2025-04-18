package com.example.luxevista.models;

import java.io.Serializable;

public class Attraction implements Serializable {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private double distance; // in km from hotel
    private String category;
    private boolean isRecommended;

    // Required empty constructor for Firebase
    public Attraction() {
    }

    public Attraction(String id, String name, String description, String imageUrl, 
                     double distance, String category, boolean isRecommended) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.distance = distance;
        this.category = category;
        this.isRecommended = isRecommended;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
    }
}
