package com.stayhub.app.data.repository;

import androidx.annotation.NonNull;

import com.stayhub.app.data.model.ApiResponse;
import com.stayhub.app.data.model.User;
import com.stayhub.app.data.model.request.CompleteRegistrationRequest;
import com.stayhub.app.data.model.request.LoginRequest;
import com.stayhub.app.data.model.request.RegisterRequest;
import com.stayhub.app.data.model.request.SendOtpRequest;
import com.stayhub.app.data.model.request.UpdateProfileRequest;
import com.stayhub.app.data.model.request.VerifyOtpRequest;
import com.stayhub.app.data.model.response.AuthResponse;
import com.stayhub.app.data.model.response.SendOtpResponse;
import com.stayhub.app.data.model.response.VerifyOtpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý các yêu cầu Xác thực (Đăng nhập, Đăng ký, Lấy profile)
 */
public class AuthRepository extends BaseRepository {

    /**
     * Gửi yêu cầu mã OTP qua Email hoặc Số điện thoại
     */
    public void sendOtp(String identifier, ApiCallback<SendOtpResponse> callback) {
        apiService.sendOtp(new SendOtpRequest(identifier)).enqueue(new Callback<ApiResponse<SendOtpResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<SendOtpResponse>> call, @NonNull Response<ApiResponse<SendOtpResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<SendOtpResponse> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<SendOtpResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    /**
     * Xác thực mã OTP người dùng nhập
     */
    public void verifyOtp(String identifier, String otp, ApiCallback<VerifyOtpResponse> callback) {
        apiService.verifyOtp(new VerifyOtpRequest(identifier, otp)).enqueue(new Callback<ApiResponse<VerifyOtpResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<VerifyOtpResponse>> call, @NonNull Response<ApiResponse<VerifyOtpResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<VerifyOtpResponse> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<VerifyOtpResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    /**
     * Hoàn tất đăng ký tài khoản mới sau khi xác thực OTP thành công
     */
    public void completeRegistration(String fullName, String email, String phone, ApiCallback<AuthResponse> callback) {
        apiService.completeRegistration(new CompleteRegistrationRequest(fullName, email, phone)).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    public void login(String email, String password, ApiCallback<AuthResponse> callback) {
        apiService.login(new LoginRequest(email, password)).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    public void register(String fullName, String email, String password, String phone, ApiCallback<AuthResponse> callback) {
        apiService.register(new RegisterRequest(fullName, email, password, phone)).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    public void getProfile(ApiCallback<User> callback) {
        apiService.getMe().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
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
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    public void updateProfile(String fullName, String phone, ApiCallback<User> callback) {
        apiService.updateProfile(new com.stayhub.app.data.model.request.UpdateProfileRequest(fullName, phone))
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<User> apiResponse = response.body();
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
                    public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                        callback.onError("Không thể kết nối đến máy chủ: " + t.getLocalizedMessage());
                    }
                });
    }
}

