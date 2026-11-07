package com.stayhub.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.stayhub.app.util.Constants;
import java.io.Serializable;

/**
 * Model Đơn đặt phòng (Booking)
 */
public class Booking implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("room_id")
    private int roomId;

    @SerializedName("check_in")
    private String checkIn;

    @SerializedName("check_out")
    private String checkOut;

    @SerializedName("guests")
    private int guests;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("status")
    private String status; // PENDING, CONFIRMED, REJECTED, CANCELLED, COMPLETED

    @SerializedName("note")
    private String note;

    @SerializedName("created_at")
    private String createdAt;

    // Các trường JOIN kèm theo từ Room và Homestay
    @SerializedName("room_name")
    private String roomName;

    @SerializedName("price_per_night")
    private double pricePerNight;

    @SerializedName("homestay_id")
    private int homestayId;

    @SerializedName("homestay_name")
    private String homestayName;

    @SerializedName("homestay_city")
    private String homestayCity;

    @SerializedName("homestay_address")
    private String homestayAddress;

    // Review status nếu có
    @SerializedName("review_id")
    private Integer reviewId;

    @SerializedName("review_rating")
    private Integer reviewRating;

    public Booking() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status != null ? status : Constants.STATUS_PENDING;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRoomName() {
        return roomName != null ? roomName : "Phòng nghỉ";
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public String getHomestayName() {
        return homestayName != null ? homestayName : "Homestay";
    }

    public void setHomestayName(String homestayName) {
        this.homestayName = homestayName;
    }

    public String getHomestayCity() {
        return homestayCity;
    }

    public void setHomestayCity(String homestayCity) {
        this.homestayCity = homestayCity;
    }

    public String getHomestayAddress() {
        return homestayAddress;
    }

    public void setHomestayAddress(String homestayAddress) {
        this.homestayAddress = homestayAddress;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Integer reviewRating) {
        this.reviewRating = reviewRating;
    }

    /**
     * Kiểm tra xem đơn này có thể hủy hay không (Chỉ khi PENDING hoặc CONFIRMED)
     */
    public boolean canCancel() {
        return Constants.STATUS_PENDING.equalsIgnoreCase(status)
                || Constants.STATUS_CONFIRMED.equalsIgnoreCase(status);
    }

    /**
     * Kiểm tra xem đơn này có thể đánh giá hay không (Đã COMPLETED và chưa từng review)
     */
    public boolean canReview() {
        return Constants.STATUS_COMPLETED.equalsIgnoreCase(status)
                && (reviewId == null || reviewId <= 0);
    }

    /**
     * Đơn đang trong trạng thái kích hoạt (Đang đặt: PENDING hoặc CONFIRMED)
     */
    public boolean isActive() {
        return Constants.STATUS_PENDING.equalsIgnoreCase(status)
                || Constants.STATUS_CONFIRMED.equalsIgnoreCase(status);
    }
}

