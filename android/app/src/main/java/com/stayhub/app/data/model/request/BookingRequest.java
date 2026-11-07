package com.stayhub.app.data.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body cho API POST /api/bookings
 */
public class BookingRequest {

    @SerializedName("room_id")
    private int roomId;

    @SerializedName("check_in")
    private String checkIn;

    @SerializedName("check_out")
    private String checkOut;

    @SerializedName("guests")
    private int guests;

    @SerializedName("note")
    private String note;

    public BookingRequest(int roomId, String checkIn, String checkOut, int guests, String note) {
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.note = note;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

