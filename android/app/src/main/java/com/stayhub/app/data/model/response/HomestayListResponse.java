package com.stayhub.app.data.model.response;

import com.google.gson.annotations.SerializedName;
import com.stayhub.app.data.model.Homestay;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO dữ liệu trả về từ GET /api/homestays
 */
public class HomestayListResponse {

    @SerializedName("homestays")
    private List<Homestay> homestays = new ArrayList<>();

    @SerializedName("pagination")
    private Pagination pagination;

    public List<Homestay> getHomestays() {
        return homestays != null ? homestays : new ArrayList<>();
    }

    public void setHomestays(List<Homestay> homestays) {
        this.homestays = homestays;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
