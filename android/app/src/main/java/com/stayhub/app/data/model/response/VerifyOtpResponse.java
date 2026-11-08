package com.stayhub.app.data.model.response;

import com.google.gson.annotations.SerializedName;
import com.stayhub.app.data.model.User;

/**
 * Phản hồi sau khi xác thực OTP thành công
 */
public class VerifyOtpResponse {

    @SerializedName("is_registered")
    private boolean registered;

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("type")
    private String type;

    @SerializedName("user")
    private User user;

    @SerializedName("token")
    private String token;

    public boolean isRegistered() {
        return registered;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}

