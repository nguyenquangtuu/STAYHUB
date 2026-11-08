package com.stayhub.app.util;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Tiện ích kiểm tra tính hợp lệ dữ liệu (Validation) chuẩn cho số điện thoại, email, họ tên
 */
public class ValidationUtils {

    // Regex kiểm tra số điện thoại Việt Nam chuẩn 10 chữ số, các đầu số mạng Viettel, Vina, Mobi, Vietnamobile...
    private static final Pattern VN_PHONE_PATTERN = Pattern.compile("^(0|\\+84)(3|5|7|8|9)[0-9]{8}$");

    public enum IdentifierType {
        EMAIL,
        PHONE,
        INVALID
    }

    /**
     * Kiểm tra số điện thoại Việt Nam hợp lệ
     */
    public static boolean isValidVietnamesePhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        String clean = phone.replaceAll("\\s+", "");
        return VN_PHONE_PATTERN.matcher(clean).matches();
    }

    /**
     * Kiểm tra email hợp lệ
     */
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    /**
     * Xác định người dùng nhập Email hay Số điện thoại hay không hợp lệ
     */
    public static IdentifierType checkIdentifierType(String input) {
        if (TextUtils.isEmpty(input)) return IdentifierType.INVALID;
        String trimmed = input.trim();
        if (trimmed.contains("@")) {
            return isValidEmail(trimmed) ? IdentifierType.EMAIL : IdentifierType.INVALID;
        }
        return isValidVietnamesePhone(trimmed) ? IdentifierType.PHONE : IdentifierType.INVALID;
    }

    /**
     * Kiểm tra họ và tên (tối thiểu 2 ký tự, không toàn khoảng trắng)
     */
    public static boolean isValidFullName(String name) {
        return name != null && name.trim().length() >= 2;
    }

    /**
     * Chuẩn hóa số điện thoại về định dạng 0...
     */
    public static String normalizePhone(String phone) {
        if (TextUtils.isEmpty(phone)) return "";
        String clean = phone.replaceAll("\\s+", "");
        if (clean.startsWith("+84")) {
            return "0" + clean.substring(3);
        }
        return clean;
    }
}
