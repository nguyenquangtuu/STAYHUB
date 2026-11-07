package com.stayhub.app.data.model.response;

import com.google.gson.annotations.SerializedName;
import com.stayhub.app.data.model.Review;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO dữ liệu trả về từ GET /api/homestays/:id/reviews
 */
public class ReviewListResponse {

    @SerializedName("reviews")
    private List<Review> reviews = new ArrayList<>();

    @SerializedName("avg_rating")
    private double avgRating;

    @SerializedName("pagination")
    private Pagination pagination;

    public List<Review> getReviews() {
        return reviews != null ? reviews : new ArrayList<>();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
