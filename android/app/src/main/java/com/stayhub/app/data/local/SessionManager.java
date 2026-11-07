package com.stayhub.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.stayhub.app.data.model.User;
import com.stayhub.app.util.Constants;

/**
 * Quản lý phiên làm việc người dùng lưu trữ trong SharedPreferences
 */
public class SessionManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
    }

    /**
     * Lưu thông tin phiên đăng nhập kèm JWT Token
     */
    public void saveSession(String token, User user) {
        editor.putString(Constants.KEY_TOKEN, token);
        if (user != null) {
            editor.putInt(Constants.KEY_USER_ID, user.getId());
            editor.putString(Constants.KEY_USER_NAME, user.getFullName());
            editor.putString(Constants.KEY_USER_EMAIL, user.getEmail());
            editor.putString(Constants.KEY_USER_PHONE, user.getPhone());
            editor.putString(Constants.KEY_USER_ROLE, user.getRole());
        }
        editor.apply();
    }

    /**
     * Lấy JWT Bearer Token hiện tại
     */
    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    /**
     * Kiểm tra trạng thái đã đăng nhập hay chưa
     */
    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.trim().isEmpty();
    }

    /**
     * Lấy thông tin người dùng đã lưu
     */
    public User getUser() {
        if (!isLoggedIn()) return null;
        int id = prefs.getInt(Constants.KEY_USER_ID, -1);
        String name = prefs.getString(Constants.KEY_USER_NAME, "");
        String email = prefs.getString(Constants.KEY_USER_EMAIL, "");
        String phone = prefs.getString(Constants.KEY_USER_PHONE, "");
        String role = prefs.getString(Constants.KEY_USER_ROLE, "user");
        return new User(id, name, email, phone, role);
    }

    /**
     * Cập nhật thông tin profile của user
     */
    public void saveUser(User user) {
        if (user != null) {
            editor.putInt(Constants.KEY_USER_ID, user.getId());
            editor.putString(Constants.KEY_USER_NAME, user.getFullName());
            editor.putString(Constants.KEY_USER_EMAIL, user.getEmail());
            editor.putString(Constants.KEY_USER_PHONE, user.getPhone());
            editor.putString(Constants.KEY_USER_ROLE, user.getRole());
            editor.apply();
        }
    }

    /**
     * Xóa sạch phiên làm việc (Đăng xuất)
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}

