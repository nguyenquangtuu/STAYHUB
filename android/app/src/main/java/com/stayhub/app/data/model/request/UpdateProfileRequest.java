package com.stayhub.app.data.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request DTO khi người dùng cập nhật thông tin cá nhân (Profile)
 */
public class UpdateProfileRequest {

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

