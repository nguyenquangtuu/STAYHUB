package com.stayhub.app.data.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request xác thực mã OTP
 */
public class VerifyOtpRequest {

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("otp")
    private String otp;

    public VerifyOtpRequest() {
    }

    public VerifyOtpRequest(String identifier, String otp) {
        this.identifier = identifier;
        this.otp = otp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
