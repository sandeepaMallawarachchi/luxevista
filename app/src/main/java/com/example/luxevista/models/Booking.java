package com.example.luxevista.models;

import java.io.Serializable;
import java.util.Date;

public class Booking implements Serializable {
    private String id;
    private String userId;
    private String itemId; // Can be roomId or serviceId
    private String itemType; // "room" or "service"
    private String itemName;
    private Date startDate;
    private Date endDate;
    private double totalPrice;
    private String status; // "confirmed", "pending", "cancelled"

    // Required empty constructor for Firebase
    public Booking() {
    }

    public Booking(String id, String userId, String itemId, String itemType, String itemName,
                  Date startDate, Date endDate, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
