package com.stayhub.app.data.model.response;

import com.google.gson.annotations.SerializedName;
import com.stayhub.app.data.model.Booking;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO dữ liệu trả về từ GET /api/bookings/my
 */
public class BookingListResponse {

    @SerializedName("bookings")
    private List<Booking> bookings = new ArrayList<>();

    @SerializedName("pagination")
    private Pagination pagination;

    public List<Booking> getBookings() {
        return bookings != null ? bookings : new ArrayList<>();
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}

