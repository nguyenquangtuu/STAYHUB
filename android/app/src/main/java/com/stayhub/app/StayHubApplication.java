package com.stayhub.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.stayhub.app.data.local.SessionManager;
import com.stayhub.app.ui.auth.LoginActivity;

/**
 * Lớp Application khởi tạo toàn cục ứng dụng StayHub
 */
public class StayHubApplication extends Application {

    private static StayHubApplication instance;
    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sessionManager = new SessionManager(this);
    }

    public static StayHubApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Tự động điều hướng về màn hình Login khi token hết hạn (HTTP 401)
     */
    public void handleUnauthorized() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
