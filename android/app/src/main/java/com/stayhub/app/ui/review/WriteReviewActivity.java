package com.stayhub.app.ui.review;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.databinding.ActivityWriteReviewBinding;
import com.stayhub.app.util.Constants;
import com.stayhub.app.viewmodel.ReviewViewModel;

/**
 * Màn hình Đánh giá Homestay sau khi hoàn thành kỳ nghỉ (COMPLETED)
 */
public class WriteReviewActivity extends AppCompatActivity {

    private ActivityWriteReviewBinding binding;
    private ReviewViewModel reviewViewModel;
    private int bookingId;
    private String homestayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingId = getIntent().getIntExtra(Constants.EXTRA_BOOKING_ID, -1);
        homestayName = getIntent().getStringExtra(Constants.EXTRA_HOMESTAY_NAME);

        if (bookingId == -1) {
            finish();
            return;
        }

        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        setupToolbar();
        setupViews();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews() {
        if (homestayName != null) {
            binding.tvReviewHomestayName.setText(homestayName);
        }

        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            int stars = (int) Math.max(1, rating);
            String ratingText;
            switch (stars) {
                case 1: ratingText = "1 / 5 sao (Rất tệ)"; break;
                case 2: ratingText = "2 / 5 sao (Chưa hài lòng)"; break;
                case 3: ratingText = "3 / 5 sao (Bình thường)"; break;
                case 4: ratingText = "4 / 5 sao (Hài lòng)"; break;
                default: ratingText = "5 / 5 sao (Rất tuyệt vời)"; break;
            }
            binding.tvRatingScore.setText(ratingText);
        });
    }

    private void setupListeners() {
        binding.btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        String comment = binding.etComment.getText() != null ? binding.etComment.getText().toString().trim() : "";
        if (comment.isEmpty()) {
            binding.tilComment.setError(getString(R.string.error_empty_comment));
            return;
        }
        binding.tilComment.setError(null);

        int rating = (int) Math.max(1, binding.ratingBar.getRating());
        reviewViewModel.submitReview(bookingId, rating, comment);
    }

    private void observeViewModel() {
        reviewViewModel.getSubmitReviewState().observe(this, state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;

                case SUCCESS:
                    setLoading(false);
                    Toast.makeText(this, getString(R.string.review_success), Toast.LENGTH_LONG).show();
                    finish();
                    break;

                case ERROR:
                    setLoading(false);
                    String message = state.getMessage() != null ? state.getMessage() : getString(R.string.error_network);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.progressBarReview.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSubmitReview.setEnabled(!isLoading);
        binding.btnSubmitReview.setText(isLoading ? "" : getString(R.string.btn_submit_review));
    }
}
