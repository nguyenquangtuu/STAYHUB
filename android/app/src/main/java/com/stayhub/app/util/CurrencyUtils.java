package com.stayhub.app.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Tiện ích định dạng tiền tệ Việt Nam Đồng (VNĐ)
 */
public final class CurrencyUtils {

    private CurrencyUtils() {
    }

    /**
     * Định dạng số tiền double thành chuỗi "X.XXX.XXX ₫"
     */
    public static String formatVnd(double amount) {
        try {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
            symbols.setGroupingSeparator('.');
            DecimalFormat formatter = new DecimalFormat("#,###", symbols);
            return formatter.format(amount) + " ₫";
        } catch (Exception e) {
            return String.format(Locale.getDefault(), "%.0f ₫", amount);
        }
    }

    /**
     * Định dạng số tiền kiểu String hoặc fallback
     */
    public static String formatVnd(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return "0 ₫";
        }
        try {
            double amount = Double.parseDouble(amountStr);
            return formatVnd(amount);
        } catch (NumberFormatException e) {
            return amountStr + " ₫";
        }
    }
}

