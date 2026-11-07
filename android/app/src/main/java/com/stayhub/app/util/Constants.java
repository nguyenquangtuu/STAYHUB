package com.stayhub.app.util;

import com.stayhub.app.BuildConfig;

/**
 * Hằng số cấu hình toàn ứng dụng StayHub
 */
public final class Constants {

    private Constants() {
        // Không khởi tạo đối tượng utility
    }

    // Base URL lấy từ BuildConfig (khai báo trong build.gradle)
    public static final String BASE_URL = BuildConfig.BASE_URL;

    // SharedPreferences Keys
    public static final String PREF_NAME = "stayhub_session_prefs";
    public static final String KEY_TOKEN = "jwt_access_token";
    public static final String KEY_USER_ID = "pref_user_id";
    public static final String KEY_USER_NAME = "pref_user_name";
    public static final String KEY_USER_EMAIL = "pref_user_email";
    public static final String KEY_USER_PHONE = "pref_user_phone";
    public static final String KEY_USER_ROLE = "pref_user_role";

    // Intent Extra Keys
    public static final String EXTRA_HOMESTAY_ID = "extra_homestay_id";
    public static final String EXTRA_ROOM_ID = "extra_room_id";
    public static final String EXTRA_ROOM_NAME = "extra_room_name";
    public static final String EXTRA_ROOM_PRICE = "extra_room_price";
    public static final String EXTRA_ROOM_CAPACITY = "extra_room_capacity";
    public static final String EXTRA_HOMESTAY_NAME = "extra_homestay_name";
    public static final String EXTRA_BOOKING_ID = "extra_booking_id";

    // Booking Statuses
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_REJECTED = "REJECTED";
}

