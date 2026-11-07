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

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
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
}
