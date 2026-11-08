package com.stayhub.app.data.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Phản hồi sau khi yêu cầu gửi mã OTP thành công
 */
public class SendOtpResponse {

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("type")
    private String type; // "phone" hoặc "email"

    @SerializedName("is_registered")
    private boolean registered;

    @SerializedName("otp_mock")
    private String otpMock;

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getOtpMock() {
        return otpMock;
    }
}

