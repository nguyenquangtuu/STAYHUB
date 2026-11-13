package com.stayhub.app.data.model.request;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Request payload cho tạo mới hoặc cập nhật Phòng nghỉ
 */
public class RoomRequest implements Serializable {

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

    public RoomRequest() {
    }

    public RoomRequest(String name, double pricePerNight, int capacity, String bedType, String status) {
        this.name = name;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.bedType = bedType;
        this.status = status != null ? status : "available";
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
}

