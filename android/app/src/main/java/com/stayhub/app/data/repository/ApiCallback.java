package com.stayhub.app.data.repository;

/**
 * Interface callback chuẩn cho các tầng Repository
 * @param <T> Dữ liệu trả về khi thành công
 */
public interface ApiCallback<T> {
    void onSuccess(T data, String message);
    void onError(String errorMessage);
}
