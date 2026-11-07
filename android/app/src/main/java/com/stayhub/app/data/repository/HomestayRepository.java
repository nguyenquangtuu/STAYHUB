package com.stayhub.app.data.repository;

import androidx.annotation.NonNull;

import com.stayhub.app.data.model.ApiResponse;
import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.data.model.response.HomestayListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý các thao tác dữ liệu Homestay
 */
public class HomestayRepository extends BaseRepository {

    public void getHomestays(String city, Double minPrice, Double maxPrice, Integer guests, String amenities, Integer page, Integer limit, ApiCallback<HomestayListResponse> callback) {
        apiService.getHomestays(city, minPrice, maxPrice, guests, amenities, page, limit).enqueue(new Callback<ApiResponse<HomestayListResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<HomestayListResponse>> call, @NonNull Response<ApiResponse<HomestayListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<HomestayListResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError(parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<HomestayListResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể tải danh sách homestay: " + t.getLocalizedMessage());
            }
        });
    }

    public void getHomestayDetail(int homestayId, ApiCallback<Homestay> callback) {
        apiService.getHomestayDetail(homestayId).enqueue(new Callback<ApiResponse<Homestay>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Homestay>> call, @NonNull Response<ApiResponse<Homestay>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Homestay> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError(parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Homestay>> call, @NonNull Throwable t) {
                callback.onError("Không thể tải chi tiết homestay: " + t.getLocalizedMessage());
            }
        });
    }
}
