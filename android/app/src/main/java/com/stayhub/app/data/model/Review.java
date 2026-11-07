package com.stayhub.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model Đánh giá Homestay
 */
public class Review implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("booking_id")
    private int bookingId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("homestay_id")
    private int homestayId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("created_at")
    private String createdAt;

    public Review() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName != null ? userName : "Khách lưu trú";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

