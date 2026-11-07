package com.stayhub.app.data.repository;

import androidx.annotation.NonNull;

import com.stayhub.app.data.model.ApiResponse;
import com.stayhub.app.data.model.Booking;
import com.stayhub.app.data.model.request.BookingRequest;
import com.stayhub.app.data.model.response.BookingListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý Đặt phòng, Lịch sử đặt phòng và Hủy đặt phòng
 */
public class BookingRepository extends BaseRepository {

    public void createBooking(int roomId, String checkIn, String checkOut, int guests, String note, ApiCallback<Booking> callback) {
        BookingRequest request = new BookingRequest(roomId, checkIn, checkOut, guests, note);
        apiService.createBooking(request).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Booking>> call, @NonNull Response<ApiResponse<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Booking> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<Booking>> call, @NonNull Throwable t) {
                callback.onError("Không thể thực hiện đặt phòng: " + t.getLocalizedMessage());
            }
        });
    }

    public void getMyBookings(Integer page, Integer limit, ApiCallback<BookingListResponse> callback) {
        apiService.getMyBookings(page, limit).enqueue(new Callback<ApiResponse<BookingListResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<BookingListResponse>> call, @NonNull Response<ApiResponse<BookingListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BookingListResponse> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<BookingListResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể tải danh sách đơn đặt phòng: " + t.getLocalizedMessage());
            }
        });
    }

    public void cancelBooking(int bookingId, ApiCallback<Booking> callback) {
        apiService.cancelBooking(bookingId).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Booking>> call, @NonNull Response<ApiResponse<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Booking> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<Booking>> call, @NonNull Throwable t) {
                callback.onError("Không thể hủy đơn đặt phòng: " + t.getLocalizedMessage());
            }
        });
    }
}

