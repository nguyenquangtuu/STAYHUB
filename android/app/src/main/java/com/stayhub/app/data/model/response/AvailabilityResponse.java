package com.stayhub.app.data.model.response;

import com.google.gson.annotations.SerializedName;
import com.stayhub.app.data.model.Room;

/**
 * DTO dữ liệu trả về từ GET /api/rooms/:id/availability
 */
public class AvailabilityResponse {

    @SerializedName("available")
    private boolean available;

    @SerializedName("reason")
    private String reason;

    @SerializedName("room")
    private Room room;

    @SerializedName("check_in")
    private String checkIn;

    @SerializedName("check_out")
    private String checkOut;

    @SerializedName("nights")
    private int nights;

    @SerializedName("total_price")
    private double totalPrice;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

