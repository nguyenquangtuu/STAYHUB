package com.stayhub.app.data.repository;

import com.google.gson.Gson;
import com.stayhub.app.data.api.ApiClient;
import com.stayhub.app.data.api.ApiService;
import com.stayhub.app.data.model.ApiResponse;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Lớp cơ sở cung cấp tiện ích phân tích dữ liệu và bóc tách thông điệp lỗi từ API
 */
public abstract class BaseRepository {

    protected final ApiService apiService;
    private final Gson gson = new Gson();

    public BaseRepository() {
        this.apiService = ApiClient.getInstance().getApiService();
    }

    /**
     * Trích xuất thông báo lỗi từ ResponseBody của Retrofit khi HTTP status != 2xx
     */
    protected String parseErrorMessage(Response<?> response) {
        if (response == null) {
            return "Phản hồi từ máy chủ không hợp lệ";
        }
        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                String errorJson = errorBody.string();
                ApiResponse<?> apiResponse = gson.fromJson(errorJson, ApiResponse.class);
                if (apiResponse != null && apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()) {
                    return apiResponse.getMessage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Lỗi máy chủ (HTTP " + response.code() + ")";
    }
}

