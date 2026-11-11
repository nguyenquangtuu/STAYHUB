package com.stayhub.app.data.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body cho API cập nhật trạng thái đơn đặt phòng (Admin)
 */
public class UpdateBookingStatusRequest {

    @SerializedName("status")
    private String status;

    public UpdateBookingStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
