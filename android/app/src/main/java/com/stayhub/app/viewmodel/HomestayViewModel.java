package com.stayhub.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.data.model.response.HomestayListResponse;
import com.stayhub.app.data.repository.ApiCallback;
import com.stayhub.app.data.repository.HomestayRepository;
import com.stayhub.app.util.UiState;

/**
 * ViewModel xử lý danh sách Homestay kèm bộ lọc và Chi tiết Homestay
 */
public class HomestayViewModel extends ViewModel {

    private final HomestayRepository homestayRepository;

    private final MutableLiveData<UiState<HomestayListResponse>> homestaysState = new MutableLiveData<>();
    private final MutableLiveData<UiState<Homestay>> homestayDetailState = new MutableLiveData<>();

    public HomestayViewModel() {
        this.homestayRepository = new HomestayRepository();
    }

    public LiveData<UiState<HomestayListResponse>> getHomestaysState() {
        return homestaysState;
    }

    public LiveData<UiState<Homestay>> getHomestayDetailState() {
        return homestayDetailState;
    }

    /**
     * Tải danh sách homestay với bộ lọc tùy chọn
     */
    public void loadHomestays(String city, Double minPrice, Double maxPrice, Integer guests, String amenities, Integer page, Integer limit) {
        homestaysState.postValue(UiState.loading());
        homestayRepository.getHomestays(city, minPrice, maxPrice, guests, amenities, page, limit, new ApiCallback<HomestayListResponse>() {
            @Override
            public void onSuccess(HomestayListResponse data, String message) {
                homestaysState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                homestaysState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Tải chi tiết homestay kèm danh sách phòng
     */
    public void loadHomestayDetail(int id) {
        homestayDetailState.postValue(UiState.loading());
        homestayRepository.getHomestayDetail(id, new ApiCallback<Homestay>() {
            @Override
            public void onSuccess(Homestay data, String message) {
                homestayDetailState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                homestayDetailState.postValue(UiState.error(errorMessage));
            }
        });
    }
}
