package com.stayhub.app.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.stayhub.app.R;
import com.stayhub.app.data.model.Booking;
import com.stayhub.app.databinding.FragmentMyBookingsBinding;
import com.stayhub.app.ui.review.WriteReviewActivity;
import com.stayhub.app.util.Constants;
import com.stayhub.app.viewmodel.BookingViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách Đơn đặt phòng cá nhân (2 tab: Đang đặt | Lịch sử)
 */
public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;
    private BookingViewModel bookingViewModel;
    private BookingAdapter bookingAdapter;

    private final List<Booking> allBookings = new ArrayList<>();
    private int currentTabPosition = 0; // 0: Đang đặt, 1: Lịch sử

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        setupRecyclerView();
        setupTabLayout();
        setupSwipeRefresh();
        observeViewModel();

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại khi quay lại từ màn hình Viết Review hoặc Đặt phòng
        loadData();
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(new BookingAdapter.OnBookingActionListener() {
            @Override
            public void onCancelBooking(Booking booking) {
                showCancelConfirmationDialog(booking);
            }

            @Override
            public void onReviewBooking(Booking booking) {
                Intent intent = new Intent(requireContext(), WriteReviewActivity.class);
                intent.putExtra(Constants.EXTRA_BOOKING_ID, booking.getId());
                intent.putExtra(Constants.EXTRA_HOMESTAY_NAME, booking.getHomestayName());
                startActivity(intent);
            }
        });

        binding.rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBookings.setAdapter(bookingAdapter);
    }

    private void setupTabLayout() {
        binding.tabLayoutBookings.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                filterBookingsByTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeColors(
                requireContext().getColor(R.color.primary),
                requireContext().getColor(R.color.secondary)
        );
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        bookingViewModel.loadMyBookings(1, 50);
    }

    private void showCancelConfirmationDialog(Booking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_cancel_title)
                .setMessage(R.string.confirm_cancel_message)
                .setPositiveButton(R.string.action_yes, (dialog, which) -> {
                    bookingViewModel.cancelBooking(booking.getId());
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    private void observeViewModel() {
        // Danh sách đơn đặt phòng
        bookingViewModel.getMyBookingsState().observe(getViewLifecycleOwner(), state -> {
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
                    allBookings.clear();
                    if (state.getData() != null && state.getData().getBookings() != null) {
                        allBookings.addAll(state.getData().getBookings());
                    }
                    filterBookingsByTab();
                    break;

                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefreshLayout.setRefreshing(false);
                    String message = state.getMessage() != null ? state.getMessage() : getString(R.string.error_network);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });

        // Kết quả hủy đơn đặt phòng
        bookingViewModel.getCancelBookingState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Đã hủy đơn đặt phòng thành công", Toast.LENGTH_SHORT).show();
                    loadData();
                    break;

                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    String msg = state.getMessage() != null ? state.getMessage() : "Không thể hủy đơn đặt phòng";
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    /**
     * Lọc danh sách đơn đặt theo 2 tab: Đang đặt (PENDING, CONFIRMED) | Lịch sử (khác)
     */
    private void filterBookingsByTab() {
        List<Booking> filteredList = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (currentTabPosition == 0) {
                // Tab "Đang đặt": PENDING hoặc CONFIRMED
                if (booking.isActive()) {
                    filteredList.add(booking);
                }
            } else {
                // Tab "Lịch sử": COMPLETED, CANCELLED, REJECTED
                if (!booking.isActive()) {
                    filteredList.add(booking);
                }
            }
        }

        bookingAdapter.setBookings(filteredList);

        if (filteredList.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvBookings.setVisibility(View.GONE);
            binding.tvEmpty.setText(currentTabPosition == 0
                    ? getString(R.string.empty_active_bookings)
                    : getString(R.string.empty_history_bookings));
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvBookings.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

