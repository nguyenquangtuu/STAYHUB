package com.stayhub.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhub.app.data.model.Review;
import com.stayhub.app.data.model.response.ReviewListResponse;
import com.stayhub.app.data.repository.ApiCallback;
import com.stayhub.app.data.repository.ReviewRepository;
import com.stayhub.app.util.UiState;

/**
 * ViewModel xử lý Đánh giá Homestay
 */
public class ReviewViewModel extends ViewModel {

    private final ReviewRepository reviewRepository;

    private final MutableLiveData<UiState<Review>> submitReviewState = new MutableLiveData<>();
    private final MutableLiveData<UiState<ReviewListResponse>> reviewsState = new MutableLiveData<>();

    public ReviewViewModel() {
        this.reviewRepository = new ReviewRepository();
    }

    public LiveData<UiState<Review>> getSubmitReviewState() {
        return submitReviewState;
    }

    public LiveData<UiState<ReviewListResponse>> getReviewsState() {
        return reviewsState;
    }

    /**
     * Gửi đánh giá cho một booking đã hoàn thành
     */
    public void submitReview(int bookingId, int rating, String comment) {
        submitReviewState.postValue(UiState.loading());
        reviewRepository.createReview(bookingId, rating, comment, new ApiCallback<Review>() {
            @Override
            public void onSuccess(Review data, String message) {
                submitReviewState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                submitReviewState.postValue(UiState.error(errorMessage));
            }
        });
    }

    /**
     * Tải danh sách đánh giá của một homestay
     */
    public void loadHomestayReviews(int homestayId, Integer page, Integer limit) {
        reviewsState.postValue(UiState.loading());
        reviewRepository.getHomestayReviews(homestayId, page, limit, new ApiCallback<ReviewListResponse>() {
            @Override
            public void onSuccess(ReviewListResponse data, String message) {
                reviewsState.postValue(UiState.success(data, message));
            }

            @Override
            public void onError(String errorMessage) {
                reviewsState.postValue(UiState.error(errorMessage));
            }
        });
    }
}
