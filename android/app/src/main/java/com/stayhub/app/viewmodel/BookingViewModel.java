package com.stayhub.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhub.app.data.model.Booking;
import com.stayhub.app.data.model.response.AvailabilityResponse;
import com.stayhub.app.data.model.response.BookingListResponse;
import com.stayhub.app.data.repository.ApiCallback;
import com.stayhub.app.data.repository.BookingRepository;
import com.stayhub.app.data.repository.RoomRepository;
import com.stayhub.app.util.UiState;

/**
 * ViewModel xử lý kiểm tra phòng trống, tạo đơn đặt phòng, lấy danh sách đặt phòng và hủy đơn
 */
public class BookingViewModel extends ViewModel {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    private final MutableLiveData<UiState<AvailabilityResponse>> availabilityState = new MutableLiveData<>();
    private final MutableLiveData<UiState<Booking>> createBookingState = new MutableLiveData<>();
    private final MutableLiveData<UiState<BookingListResponse>> myBookingsState = new MutableLiveData<>();
    private final MutableLiveData<UiState<Booking>> cancelBookingState = new MutableLiveData<>();
    private final MutableLiveData<UiState<BookingListResponse>> adminBookingsState = new MutableLiveData<>();
    private final MutableLiveData<UiState<Booking>> updateStatusState = new MutableLiveData<>();

    public BookingViewModel() {
        this.roomRepository = new RoomRepository();
        this.bookingRepository = new BookingRepository();
    }

    public LiveData<UiState<AvailabilityResponse>> getAvailabilityState() {
        return availabilityState;
    }

    public LiveData<UiState<Booking>> getCreateBookingState() {
        return createBookingState;
    }

    public LiveData<UiState<BookingListResponse>> getMyBookingsState() {
        return myBookingsState;
    }

    public LiveData<UiState<Booking>> getCancelBookingState() {
        return cancelBookingState;
    }

    /**
     * Kiểm tra phòng còn trống trong khoảng ngày hay không
     */
    public void checkAvailability(int roomId, String checkIn, String checkOut) {
        availabilityState.postValue(UiState.loading());
        roomRepository.checkAvailability(roomId, checkIn, checkOut, new ApiCallback<AvailabilityResponse>() {
            @Override
            public void onSuccess(AvailabilityResponse data, String message) {
                availabilityState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                availabilityState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Tạo đơn đặt phòng mới
     */
    public void createBooking(int roomId, String checkIn, String checkOut, int guests, String note) {
        createBookingState.postValue(UiState.loading());
        bookingRepository.createBooking(roomId, checkIn, checkOut, guests, note, new ApiCallback<Booking>() {
            @Override
            public void onSuccess(Booking data, String message) {
                createBookingState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                createBookingState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Tải danh sách các đơn đặt phòng của người dùng hiện tại
     */
    public void loadMyBookings(Integer page, Integer limit) {
        myBookingsState.postValue(UiState.loading());
        bookingRepository.getMyBookings(page, limit, new ApiCallback<BookingListResponse>() {
            @Override
            public void onSuccess(BookingListResponse data, String message) {
                myBookingsState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                myBookingsState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Hủy đơn đặt phòng (áp dụng khi PENDING hoặc CONFIRMED)
     */
    public void cancelBooking(int bookingId) {
        cancelBookingState.postValue(UiState.loading());
        bookingRepository.cancelBooking(bookingId, new ApiCallback<Booking>() {
            @Override
            public void onSuccess(Booking data, String message) {
                cancelBookingState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                cancelBookingState.postValue(UiState.error(errorMessage));
            }
        });
    }

    public LiveData<UiState<BookingListResponse>> getAdminBookingsState() {
        return adminBookingsState;
    }

    public LiveData<UiState<Booking>> getUpdateStatusState() {
        return updateStatusState;
    }

    /**
     * Tải danh sách đơn đặt phòng toàn hệ thống (Dành cho Admin)
     */
    public void loadAdminBookings(String status, Integer homestayId, Integer page, Integer limit) {
        adminBookingsState.postValue(UiState.loading());
        bookingRepository.getAdminBookings(status, homestayId, page, limit, new ApiCallback<BookingListResponse>() {
            @Override
            public void onSuccess(BookingListResponse data, String message) {
                adminBookingsState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                adminBookingsState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Cập nhật trạng thái đơn đặt phòng (Admin duyệt / từ chối / hoàn thành)
     */
    public void updateBookingStatus(int bookingId, String newStatus) {
        updateStatusState.postValue(UiState.loading());
        bookingRepository.updateBookingStatus(bookingId, newStatus, new ApiCallback<Booking>() {
            @Override
            public void onSuccess(Booking data, String message) {
                updateStatusState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                updateStatusState.postValue(UiState.error(errorMessage));
            }
        });
    }
}

