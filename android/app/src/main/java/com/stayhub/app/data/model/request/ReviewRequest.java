package com.stayhub.app.data.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body cho API POST /api/reviews
 */
public class ReviewRequest {

    @SerializedName("booking_id")
    private int bookingId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    public ReviewRequest(int bookingId, int rating, String comment) {
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
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
}

