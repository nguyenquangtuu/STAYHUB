package com.stayhub.app.ui.homestay;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.data.model.Room;
import com.stayhub.app.databinding.ActivityHomestayDetailBinding;
import com.stayhub.app.ui.booking.BookingActivity;
import com.stayhub.app.ui.review.ReviewListAdapter;
import com.stayhub.app.util.Constants;
import com.stayhub.app.viewmodel.HomestayViewModel;
import com.stayhub.app.viewmodel.ReviewViewModel;

import java.util.List;
import java.util.Locale;

/**
 * Màn hình Chi tiết Homestay: Xem phòng, tiện ích và đánh giá
 */
public class HomestayDetailActivity extends AppCompatActivity {

    private ActivityHomestayDetailBinding binding;
    private HomestayViewModel homestayViewModel;
    private ReviewViewModel reviewViewModel;
    private RoomListAdapter roomListAdapter;
    private ReviewListAdapter reviewListAdapter;
    private int homestayId;
    private Homestay currentHomestay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomestayDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        homestayId = getIntent().getIntExtra(Constants.EXTRA_HOMESTAY_ID, -1);
        if (homestayId == -1) {
            finish();
            return;
        }

        homestayViewModel = new ViewModelProvider(this).get(HomestayViewModel.class);
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        setupToolbar();
        setupRecyclerViews();
        observeViewModel();

        // Tải chi tiết homestay & danh sách review
        homestayViewModel.loadHomestayDetail(homestayId);
        reviewViewModel.loadHomestayReviews(homestayId, 1, 10);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerViews() {
        // Adapter phòng nghỉ
        roomListAdapter = new RoomListAdapter(this::navigateToBooking);
        binding.rvRooms.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRooms.setAdapter(roomListAdapter);

        // Adapter đánh giá
        reviewListAdapter = new ReviewListAdapter();
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(reviewListAdapter);
    }

    private void navigateToBooking(Room room) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra(Constants.EXTRA_ROOM_ID, room.getId());
        intent.putExtra(Constants.EXTRA_ROOM_NAME, room.getName());
        intent.putExtra(Constants.EXTRA_ROOM_PRICE, room.getPricePerNight());
        intent.putExtra(Constants.EXTRA_ROOM_CAPACITY, room.getCapacity());
        if (currentHomestay != null) {
            intent.putExtra(Constants.EXTRA_HOMESTAY_NAME, currentHomestay.getName());
        }
        startActivity(intent);
    }

    private void observeViewModel() {
        // Lắng nghe chi tiết Homestay
        homestayViewModel.getHomestayDetailState().observe(this, state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (state.getData() != null) {
                        currentHomestay = state.getData();
                        bindHomestayData(currentHomestay);
                    }
                    break;

                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    String message = state.getMessage() != null ? state.getMessage() : getString(R.string.error_network);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });

        // Lắng nghe danh sách đánh giá
        reviewViewModel.getReviewsState().observe(this, state -> {
            if (state == null) return;

            if (state.isSuccess() && state.getData() != null) {
                if (!state.getData().getReviews().isEmpty()) {
                    reviewListAdapter.setReviews(state.getData().getReviews());
                    binding.rvReviews.setVisibility(View.VISIBLE);
                    binding.tvEmptyReviews.setVisibility(View.GONE);
                } else {
                    binding.rvReviews.setVisibility(View.GONE);
                    binding.tvEmptyReviews.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void bindHomestayData(Homestay homestay) {
        binding.collapsingToolbar.setTitle(homestay.getName());
        binding.tvDetailName.setText(homestay.getName());
        binding.tvDetailLocation.setText(homestay.getCity());
        binding.tvDetailAddress.setText(homestay.getAddress());

        if (homestay.getAvgRating() > 0) {
            binding.tvDetailRating.setText(String.format(Locale.getDefault(), "%.1f", homestay.getAvgRating()));
        } else {
            binding.tvDetailRating.setText("Mới");
        }

        binding.tvDetailDescription.setText(
                homestay.getDescription() != null && !homestay.getDescription().isEmpty()
                        ? homestay.getDescription()
                        : "Chưa có thông tin mô tả chi tiết."
        );

        // Tải ảnh bìa qua Glide
        Glide.with(this)
                .load(homestay.getImageUrl())
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .centerCrop()
                .into(binding.imgDetailHomestay);

        // Hiển thị Chips tiện ích gọn gàng và thẩm mỹ
        binding.chipGroupAmenities.removeAllViews();
        List<String> displayAmenities = homestay.getDisplayAmenities();
        if (displayAmenities != null && !displayAmenities.isEmpty()) {
            for (String amenity : displayAmenities) {
                Chip chip = new Chip(this);
                chip.setText(getAmenityDisplayName(amenity));
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setEnsureMinTouchTargetSize(false);
                chip.setChipMinHeightResource(R.dimen.chip_height);
                chip.setChipCornerRadiusResource(R.dimen.chip_corner_radius);
                chip.setChipBackgroundColorResource(R.color.surface_variant);
                chip.setTextColor(getColor(R.color.text_primary));
                chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                int iconRes = getAmenityIcon(amenity);
                if (iconRes != 0) {
                    chip.setChipIconResource(iconRes);
                    chip.setChipIconTintResource(R.color.primary);
                    chip.setChipIconSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
                    chip.setChipIconVisible(true);
                }

                binding.chipGroupAmenities.addView(chip);
            }
        }

        // Gán danh sách phòng
        if (homestay.getRooms() != null && !homestay.getRooms().isEmpty()) {
            roomListAdapter.setRooms(homestay.getRooms());
            binding.rvRooms.setVisibility(View.VISIBLE);
            binding.tvEmptyRooms.setVisibility(View.GONE);
        } else {
            binding.rvRooms.setVisibility(View.GONE);
            binding.tvEmptyRooms.setVisibility(View.VISIBLE);
        }
    }

    private int getAmenityIcon(String rawAmenity) {
        if (rawAmenity == null) return R.drawable.ic_check_circle;
        switch (rawAmenity.toLowerCase().trim()) {
            case "wifi": return R.drawable.ic_wifi;
            case "parking": return R.drawable.ic_parking;
            case "kitchen": return R.drawable.ic_kitchen;
            case "pool": return R.drawable.ic_pool;
            case "air_conditioning": return R.drawable.ic_hotel;
            case "beach_front":
            case "mountain_view":
            case "garden":
            case "balcony":
                return R.drawable.ic_location;
            default: return R.drawable.ic_check_circle;
        }
    }

    private String getAmenityDisplayName(String rawAmenity) {
        switch (rawAmenity.toLowerCase().trim()) {
            case "wifi": return "Wifi tốc độ cao";
            case "parking": return "Bãi đỗ xe";
            case "kitchen": return "Bếp nấu";
            case "air_conditioning": return "Điều hòa";
            case "pool": return "Hồ bơi";
            case "mountain_view": return "View núi";
            case "beach_front": return "Sát biển";
            case "bbq": return "Bếp nướng BBQ";
            case "garden": return "Sân vườn";
            case "balcony": return "Ban công";
            default: return rawAmenity;
        }
    }
}

