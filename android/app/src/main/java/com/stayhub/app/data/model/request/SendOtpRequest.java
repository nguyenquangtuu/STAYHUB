package com.stayhub.app.data.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request gửi mã xác thực OTP qua Số điện thoại hoặc Email
 */
public class SendOtpRequest {

    @SerializedName("identifier")
    private String identifier;

    public SendOtpRequest() {
    }

    public SendOtpRequest(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}

