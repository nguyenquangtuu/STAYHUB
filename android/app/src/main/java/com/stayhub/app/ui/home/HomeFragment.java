package com.stayhub.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.databinding.FragmentHomeBinding;
import com.stayhub.app.ui.homestay.HomestayDetailActivity;
import com.stayhub.app.util.Constants;
import com.stayhub.app.util.CurrencyUtils;
import com.stayhub.app.viewmodel.HomestayViewModel;

/**
 * Fragment hiển thị danh sách Homestay kèm bộ lọc tìm kiếm
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomestayViewModel homestayViewModel;
    private HomestayAdapter homestayAdapter;

    private static final int MAX_PRICE_DEFAULT = 5000000;
    private int currentMaxPrice = MAX_PRICE_DEFAULT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homestayViewModel = new ViewModelProvider(this).get(HomestayViewModel.class);

        setupRecyclerView();
        setupFilterBar();
        setupSwipeRefresh();
        observeViewModel();

        // Tải dữ liệu lần đầu
        fetchHomestays();
    }

    private void setupRecyclerView() {
        homestayAdapter = new HomestayAdapter(homestay -> {
            Intent intent = new Intent(requireContext(), HomestayDetailActivity.class);
            intent.putExtra(Constants.EXTRA_HOMESTAY_ID, homestay.getId());
            startActivity(intent);
        });

        binding.rvHomestays.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHomestays.setAdapter(homestayAdapter);
    }

    private void setupFilterBar() {
        updatePriceLabel(MAX_PRICE_DEFAULT);

        binding.seekBarPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Bước giá tối thiểu 100.000 VNĐ
                currentMaxPrice = Math.max(100000, (progress / 100000) * 100000);
                updatePriceLabel(currentMaxPrice);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.btnApplyFilter.setOnClickListener(v -> fetchHomestays());

        binding.btnResetFilter.setOnClickListener(v -> {
            binding.etSearchCity.setText("");
            binding.seekBarPrice.setProgress(MAX_PRICE_DEFAULT);
            currentMaxPrice = MAX_PRICE_DEFAULT;
            updatePriceLabel(MAX_PRICE_DEFAULT);
            fetchHomestays();
        });
    }

    private void updatePriceLabel(int price) {
        binding.tvPriceFilterLabel.setText(String.format(
                getString(R.string.filter_max_price),
                CurrencyUtils.formatVnd(price)
        ));
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeColors(
                requireContext().getColor(R.color.primary),
                requireContext().getColor(R.color.secondary)
        );
        binding.swipeRefreshLayout.setOnRefreshListener(this::fetchHomestays);
    }

    /**
     * Gọi API lấy danh sách homestay dựa trên bộ lọc
     */
    private void fetchHomestays() {
        String city = binding.etSearchCity.getText() != null
                ? binding.etSearchCity.getText().toString().trim()
                : null;
        if (city != null && city.isEmpty()) city = null;

        Double maxPrice = currentMaxPrice < MAX_PRICE_DEFAULT ? (double) currentMaxPrice : null;

        homestayViewModel.loadHomestays(city, null, maxPrice, null, null, 1, 20);
    }

    private void observeViewModel() {
        homestayViewModel.getHomestaysState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    if (!binding.swipeRefreshLayout.isRefreshing()) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }
                    binding.layoutEmpty.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefreshLayout.setRefreshing(false);

                    if (state.getData() != null && !state.getData().getHomestays().isEmpty()) {
                        homestayAdapter.setHomestays(state.getData().getHomestays());
                        binding.layoutEmpty.setVisibility(View.GONE);
                        binding.rvHomestays.setVisibility(View.VISIBLE);
                    } else {
                        homestayAdapter.setHomestays(null);
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                        binding.rvHomestays.setVisibility(View.GONE);
                    }
                    break;

                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefreshLayout.setRefreshing(false);
                    String message = state.getMessage() != null ? state.getMessage() : getString(R.string.error_network);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, v -> fetchHomestays())
                            .show();
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

