package com.stayhub.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp Generic bao bọc định dạng phản hồi chuẩn từ Backend RESTful API
 * {
 *   "success": true,
 *   "message": "Thông điệp",
 *   "data": { ... },
 *   "error": { "code": 400, "message": "..." }
 * }
 * @param <T> Kiểu dữ liệu chứa trong trường data
 */
public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private T data;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private ErrorPayload error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        if (message != null && !message.isEmpty()) {
            return message;
        }
        if (error != null && error.getMessage() != null) {
            return error.getMessage();
        }
        return "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorPayload getError() {
        return error;
    }

    public void setError(ErrorPayload error) {
        this.error = error;
    }

    public static class ErrorPayload {
        @SerializedName("code")
        private Object code;

        @SerializedName("message")
        private String message;

        public Object getCode() {
            return code;
        }

        public void setCode(Object code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

