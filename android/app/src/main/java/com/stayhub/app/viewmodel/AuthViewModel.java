package com.stayhub.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhub.app.data.model.User;
import com.stayhub.app.data.model.response.AuthResponse;
import com.stayhub.app.data.repository.ApiCallback;
import com.stayhub.app.data.repository.AuthRepository;
import com.stayhub.app.util.UiState;

/**
 * ViewModel xử lý Đăng nhập, Đăng ký và Thông tin tài khoản người dùng
 */
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<UiState<AuthResponse>> loginState = new MutableLiveData<>();
    private final MutableLiveData<UiState<AuthResponse>> registerState = new MutableLiveData<>();
    private final MutableLiveData<UiState<User>> profileState = new MutableLiveData<>();
    private final MutableLiveData<UiState<User>> updateProfileState = new MutableLiveData<>();

    // Các trạng thái cho luồng xác thực một chạm OTP
    private final MutableLiveData<UiState<com.stayhub.app.data.model.response.SendOtpResponse>> sendOtpState = new MutableLiveData<>();
    private final MutableLiveData<UiState<com.stayhub.app.data.model.response.VerifyOtpResponse>> verifyOtpState = new MutableLiveData<>();
    private final MutableLiveData<UiState<AuthResponse>> completeRegistrationState = new MutableLiveData<>();

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<UiState<com.stayhub.app.data.model.response.SendOtpResponse>> getSendOtpState() {
        return sendOtpState;
    }

    public LiveData<UiState<com.stayhub.app.data.model.response.VerifyOtpResponse>> getVerifyOtpState() {
        return verifyOtpState;
    }

    public LiveData<UiState<AuthResponse>> getCompleteRegistrationState() {
        return completeRegistrationState;
    }

    public LiveData<UiState<AuthResponse>> getLoginState() {
        return loginState;
    }

    public LiveData<UiState<AuthResponse>> getRegisterState() {
        return registerState;
    }

    public LiveData<UiState<User>> getProfileState() {
        return profileState;
    }

    public LiveData<UiState<User>> getUpdateProfileState() {
        return updateProfileState;
    }

    /**
     * Yêu cầu gửi mã xác thực OTP
     */
    public void sendOtp(String identifier) {
        sendOtpState.postValue(UiState.loading());
        authRepository.sendOtp(identifier, new ApiCallback<com.stayhub.app.data.model.response.SendOtpResponse>() {
            @Override
            public void onSuccess(com.stayhub.app.data.model.response.SendOtpResponse data, String message) {
                sendOtpState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                sendOtpState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Xác thực mã OTP
     */
    public void verifyOtp(String identifier, String otp) {
        verifyOtpState.postValue(UiState.loading());
        authRepository.verifyOtp(identifier, otp, new ApiCallback<com.stayhub.app.data.model.response.VerifyOtpResponse>() {
            @Override
            public void onSuccess(com.stayhub.app.data.model.response.VerifyOtpResponse data, String message) {
                verifyOtpState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                verifyOtpState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Hoàn tất đăng ký tài khoản mới sau khi xác thực OTP thành công
     */
    public void completeRegistration(String fullName, String email, String phone) {
        completeRegistrationState.postValue(UiState.loading());
        authRepository.completeRegistration(fullName, email, phone, new ApiCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data, String message) {
                completeRegistrationState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                completeRegistrationState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Thực hiện đăng nhập
     */
    public void login(String email, String password) {
        loginState.postValue(UiState.loading());
        authRepository.login(email, password, new ApiCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data, String message) {
                loginState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                loginState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Thực hiện đăng ký tài khoản mới
     */
    public void register(String fullName, String email, String password, String phone) {
        registerState.postValue(UiState.loading());
        authRepository.register(fullName, email, password, phone, new ApiCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data, String message) {
                registerState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                registerState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Tải thông tin tài khoản hiện tại từ máy chủ
     */
    public void loadProfile() {
        profileState.postValue(UiState.loading());
        authRepository.getProfile(new ApiCallback<User>() {
            @Override
            public void onSuccess(User data, String message) {
                profileState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                profileState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Cập nhật thông tin tài khoản người dùng
     */
    public void updateProfile(String fullName, String phone) {
        updateProfileState.postValue(UiState.loading());
        authRepository.updateProfile(fullName, phone, new ApiCallback<User>() {
            @Override
            public void onSuccess(User data, String message) {
                updateProfileState.postValue(UiState.success(data, message));
                profileState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                updateProfileState.postValue(UiState.error(errorMessage));
            }
        });
    }
}

