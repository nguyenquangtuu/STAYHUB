package com.stayhub.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhub.app.StayHubApplication;
import com.stayhub.app.data.local.SessionManager;
import com.stayhub.app.databinding.ActivitySplashBinding;
import com.stayhub.app.ui.auth.LoginActivity;
import com.stayhub.app.ui.home.MainActivity;

/**
 * Màn hình Splash: Kiểm tra JWT Token trong SharedPreferences để điều hướng
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1200;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Trì hoãn 1.2s tạo trải nghiệm mượt mà, sau đó kiểm tra phiên đăng nhập
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthSession, SPLASH_DELAY_MS);
    }

    private void checkAuthSession() {
        SessionManager sessionManager = StayHubApplication.getInstance().getSessionManager();
        Intent intent;

        if (sessionManager != null && sessionManager.isLoggedIn()) {
            // Đã đăng nhập -> chuyển sang màn hình chính
            intent = new Intent(this, MainActivity.class);
        } else {
            // Chưa đăng nhập -> chuyển sang màn hình đăng nhập
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}

