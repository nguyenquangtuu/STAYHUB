package com.stayhub.app.data.api;

import androidx.annotation.NonNull;

import com.stayhub.app.StayHubApplication;
import com.stayhub.app.data.local.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor tự động gắn Bearer Token vào Header và xử lý tự động đăng xuất khi gặp mã lỗi 401
 */
public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        // Gắn header Authorization nếu đã có token đăng nhập
        if (sessionManager != null && sessionManager.isLoggedIn()) {
            String token = sessionManager.getToken();
            if (token != null && !token.trim().isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }
        }

        requestBuilder.header("Accept", "application/json");
        requestBuilder.header("Content-Type", "application/json");

        Response response = chain.proceed(requestBuilder.build());

        // Bắt lỗi HTTP 401 Unauthorized -> Tự động đăng xuất & chuyển về LoginActivity
        if (response.code() == 401) {
            if (sessionManager != null && sessionManager.isLoggedIn()) {
                StayHubApplication app = StayHubApplication.getInstance();
                if (app != null) {
                    app.handleUnauthorized();
                }
            }
        }

        return response;
    }
}

