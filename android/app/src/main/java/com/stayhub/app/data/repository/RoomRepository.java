package com.stayhub.app.data.repository;

import androidx.annotation.NonNull;

import com.stayhub.app.data.model.ApiResponse;
import com.stayhub.app.data.model.Room;
import com.stayhub.app.data.model.request.RoomRequest;
import com.stayhub.app.data.model.response.AvailabilityResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý các thao tác dữ liệu Phòng và Kiểm tra tình trạng phòng trống
 */
public class RoomRepository extends BaseRepository {

    public void getRoomsByHomestay(int homestayId, ApiCallback<List<Room>> callback) {
        apiService.getHomestayRooms(homestayId).enqueue(new Callback<ApiResponse<List<Room>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Room>>> call, @NonNull Response<ApiResponse<List<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Room>> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<List<Room>>> call, @NonNull Throwable t) {
                callback.onError("Không thể tải danh sách phòng: " + t.getLocalizedMessage());
            }
        });
    }

    public void checkAvailability(int roomId, String checkIn, String checkOut, ApiCallback<AvailabilityResponse> callback) {
        apiService.checkRoomAvailability(roomId, checkIn, checkOut).enqueue(new Callback<ApiResponse<AvailabilityResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AvailabilityResponse>> call, @NonNull Response<ApiResponse<AvailabilityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AvailabilityResponse> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<AvailabilityResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể kiểm tra tình trạng phòng: " + t.getLocalizedMessage());
            }
        });
    }

    public void createRoom(int homestayId, RoomRequest request, ApiCallback<Room> callback) {
        apiService.createRoom(homestayId, request).enqueue(new Callback<ApiResponse<Room>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Room>> call, @NonNull Response<ApiResponse<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Room> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<Room>> call, @NonNull Throwable t) {
                callback.onError("Không thể tạo phòng mới: " + t.getLocalizedMessage());
            }
        });
    }

    public void updateRoom(int roomId, RoomRequest request, ApiCallback<Room> callback) {
        apiService.updateRoom(roomId, request).enqueue(new Callback<ApiResponse<Room>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Room>> call, @NonNull Response<ApiResponse<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Room> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<Room>> call, @NonNull Throwable t) {
                callback.onError("Không thể cập nhật phòng: " + t.getLocalizedMessage());
            }
        });
    }

    public void deleteRoom(int roomId, ApiCallback<Void> callback) {
        apiService.deleteRoom(roomId).enqueue(new Callback<ApiResponse<Void>>() {
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
                callback.onError("Không thể xóa phòng: " + t.getLocalizedMessage());
            }
        });
    }
}

