package com.stayhub.app.data.repository;

import androidx.annotation.NonNull;

import com.stayhub.app.data.model.ApiResponse;
import com.stayhub.app.data.model.Review;
import com.stayhub.app.data.model.request.ReviewRequest;
import com.stayhub.app.data.model.response.ReviewListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý Đánh giá Homestay
 */
public class ReviewRepository extends BaseRepository {

    public void createReview(int bookingId, int rating, String comment, ApiCallback<Review> callback) {
        ReviewRequest request = new ReviewRequest(bookingId, rating, comment);
        apiService.createReview(request).enqueue(new Callback<ApiResponse<Review>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Review>> call, @NonNull Response<ApiResponse<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Review> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<Review>> call, @NonNull Throwable t) {
                callback.onError("Không thể gửi đánh giá: " + t.getLocalizedMessage());
            }
        });
    }

    public void getHomestayReviews(int homestayId, Integer page, Integer limit, ApiCallback<ReviewListResponse> callback) {
        apiService.getHomestayReviews(homestayId, page, limit).enqueue(new Callback<ApiResponse<ReviewListResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ReviewListResponse>> call, @NonNull Response<ApiResponse<ReviewListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ReviewListResponse> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<ReviewListResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể tải danh sách đánh giá: " + t.getLocalizedMessage());
            }
        });
    }

    public void deleteReview(int reviewId, ApiCallback<Void> callback) {
        apiService.deleteReview(reviewId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(null, apiResponse.getMessage());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError(parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onError("Không thể xóa đánh giá: " + t.getLocalizedMessage());
            }
        });
    }
}

