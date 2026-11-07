package com.stayhub.app.data.model.response;

import com.google.gson.annotations.SerializedName;
import com.stayhub.app.data.model.User;

/**
 * DTO dữ liệu trả về sau khi Đăng ký / Đăng nhập thành công
 */
public class AuthResponse {

    @SerializedName("user")
    private User user;

    @SerializedName("token")
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

