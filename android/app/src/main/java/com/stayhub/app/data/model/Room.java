package com.stayhub.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model Phòng nghỉ trong Homestay
 */
public class Room implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("homestay_id")
    private int homestayId;

    @SerializedName("name")
    private String name;

    @SerializedName("price_per_night")
    private double pricePerNight;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("bed_type")
    private String bedType;

    @SerializedName("status")
    private String status; // 'available' hoặc 'maintenance'

    public Room() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return "available".equalsIgnoreCase(status);
    }
}

