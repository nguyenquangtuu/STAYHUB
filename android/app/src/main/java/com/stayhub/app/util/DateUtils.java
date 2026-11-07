package com.stayhub.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Tiện ích xử lý định dạng và tính toán ngày tháng
 */
public final class DateUtils {

    private static final String API_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";

    private DateUtils() {
    }

    /**
     * Chuyển từ định dạng API (yyyy-MM-dd) sang định dạng hiển thị UI (dd/MM/yyyy)
     */
    public static String toDisplayDate(String apiDateStr) {
        if (apiDateStr == null || apiDateStr.trim().isEmpty()) {
            return "";
        }
        try {
            // Trường hợp backend trả về ISO timestamp (e.g. 2026-11-15T00:00:00.000Z)
            if (apiDateStr.length() > 10) {
                apiDateStr = apiDateStr.substring(0, 10);
            }
            SimpleDateFormat apiFormat = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
            Date date = apiFormat.parse(apiDateStr);
            if (date != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
                return displayFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return apiDateStr;
    }

    /**
     * Chuyển từ Date sang chuỗi yyyy-MM-dd gửi lên Backend
     */
    public static String toApiDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Chuyển từ Date sang chuỗi dd/MM/yyyy hiển thị trên UI
     */
    public static String toDisplayDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Parse chuỗi yyyy-MM-dd thành đối tượng Date
     */
    public static Date parseApiDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            if (dateStr.length() > 10) {
                dateStr = dateStr.substring(0, 10);
            }
            SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Tính số đêm giữa 2 ngày check-in và check-out (yyyy-MM-dd)
     */
    public static int calculateNights(String checkInStr, String checkOutStr) {
        Date checkIn = parseApiDate(checkInStr);
        Date checkOut = parseApiDate(checkOutStr);
        if (checkIn == null || checkOut == null) return 0;

        long diffInMillis = checkOut.getTime() - checkIn.getTime();
        long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        return (int) Math.max(0, diffInDays);
    }

    /**
     * Lấy ngày hôm nay định dạng yyyy-MM-dd
     */
    public static String getTodayApiDate() {
        return toApiDate(new Date());
    }

    /**
     * Lấy ngày mai định dạng yyyy-MM-dd
     */
    public static String getTomorrowApiDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return toApiDate(cal.getTime());
    }
}
