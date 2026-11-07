package com.stayhub.app.util;

/**
 * Lớp đóng gói trạng thái UI State cho LiveData trong kiến trúc MVVM
 * @param <T> Kiểu dữ liệu tương ứng khi thành công
 */
public class UiState<T> {

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final T data;
    private final String message;

    private UiState(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(Status.LOADING, null, null);
    }

    public static <T> UiState<T> success(T data) {
        return new UiState<>(Status.SUCCESS, data, null);
    }

    public static <T> UiState<T> success(T data, String message) {
        return new UiState<>(Status.SUCCESS, data, message);
    }

    public static <T> UiState<T> error(String message) {
        return new UiState<>(Status.ERROR, null, message);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }
}

