package com.stayhub.app.ui.admin;

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
import com.stayhub.app.databinding.FragmentAdminBinding;
import com.stayhub.app.util.Constants;
import com.stayhub.app.util.CurrencyUtils;
import com.stayhub.app.viewmodel.BookingViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment Quản trị viên (Admin Dashboard)
 * Cho phép Admin theo dõi thống kê, duyệt đơn PENDING, từ chối hoặc hoàn tất đơn
 */
public class AdminFragment extends Fragment {

    private FragmentAdminBinding binding;
    private BookingViewModel bookingViewModel;
    private AdminBookingAdapter adapter;

    private final List<Booking> allAdminBookings = new ArrayList<>();
    private int selectedTabPosition = 0; // 0: Tất cả, 1: PENDING, 2: CONFIRMED, 3: COMPLETED, 4: CANCELLED

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminBinding.inflate(inflater, container, false);
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
        loadData();
    }

    private void setupRecyclerView() {
        adapter = new AdminBookingAdapter(new AdminBookingAdapter.OnAdminActionListener() {
            @Override
            public void onApprove(Booking booking) {
                showApproveConfirmationDialog(booking);
            }

            @Override
            public void onReject(Booking booking) {
                showRejectConfirmationDialog(booking);
            }

            @Override
            public void onComplete(Booking booking) {
                showCompleteConfirmationDialog(booking);
            }
        });

        binding.rvAdminBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminBookings.setAdapter(adapter);
    }

    private void setupTabLayout() {
        binding.tabLayoutAdmin.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tab.getPosition();
                filterAndDisplayBookings();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshAdmin.setColorSchemeColors(
                requireContext().getColor(R.color.primary),
                requireContext().getColor(R.color.secondary)
        );
        binding.swipeRefreshAdmin.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        // Tải toàn bộ đơn (trang 1, 100 đơn) để phục vụ thống kê và bộ lọc
        bookingViewModel.loadAdminBookings(null, null, 1, 100);
    }

    private void observeViewModel() {
        // Quan sát danh sách đơn đặt phòng Admin
        bookingViewModel.getAdminBookingsState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    if (!binding.swipeRefreshAdmin.isRefreshing()) {
                        binding.progressAdmin.setVisibility(View.VISIBLE);
                    }
                    binding.layoutAdminEmpty.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    binding.progressAdmin.setVisibility(View.GONE);
                    binding.swipeRefreshAdmin.setRefreshing(false);
                    allAdminBookings.clear();
                    if (state.getData() != null && state.getData().getBookings() != null) {
                        allAdminBookings.addAll(state.getData().getBookings());
                    }
                    updateDashboardStats();
                    filterAndDisplayBookings();
                    break;

                case ERROR:
                    binding.progressAdmin.setVisibility(View.GONE);
                    binding.swipeRefreshAdmin.setRefreshing(false);
                    String msg = state.getMessage() != null ? state.getMessage() : getString(R.string.error_network);
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });

        // Quan sát kết quả cập nhật trạng thái đơn (Duyệt/Từ chối/Hoàn tất)
        bookingViewModel.getUpdateStatusState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    binding.progressAdmin.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressAdmin.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), getString(R.string.admin_action_success), Toast.LENGTH_SHORT).show();
                    loadData();
                    break;

                case ERROR:
                    binding.progressAdmin.setVisibility(View.GONE);
                    String err = state.getMessage() != null ? state.getMessage() : "Cập nhật thất bại";
                    Snackbar.make(binding.getRoot(), err, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    /**
     * Cập nhật 3 chỉ số thống kê trên Dashboard Admin
     */
    private void updateDashboardStats() {
        int total = allAdminBookings.size();
        int pendingCount = 0;
        double totalRevenue = 0.0;

        for (Booking booking : allAdminBookings) {
            if (Constants.STATUS_PENDING.equalsIgnoreCase(booking.getStatus())) {
                pendingCount++;
            }
            if (Constants.STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())
                    || Constants.STATUS_COMPLETED.equalsIgnoreCase(booking.getStatus())) {
                totalRevenue += booking.getTotalPrice();
            }
        }

        binding.tvStatTotalBookings.setText(String.valueOf(total));
        binding.tvStatPendingBookings.setText(String.valueOf(pendingCount));
        binding.tvStatRevenue.setText(CurrencyUtils.formatVnd(totalRevenue));
    }

    /**
     * Lọc danh sách theo Tab đang chọn
     */
    private void filterAndDisplayBookings() {
        List<Booking> filtered = new ArrayList<>();

        for (Booking b : allAdminBookings) {
            String status = b.getStatus();
            switch (selectedTabPosition) {
                case 0: // Tất cả
                    filtered.add(b);
                    break;
                case 1: // Chờ duyệt (PENDING)
                    if (Constants.STATUS_PENDING.equalsIgnoreCase(status)) {
                        filtered.add(b);
                    }
                    break;
                case 2: // Đã duyệt (CONFIRMED)
                    if (Constants.STATUS_CONFIRMED.equalsIgnoreCase(status)) {
                        filtered.add(b);
                    }
                    break;
                case 3: // Hoàn thành (COMPLETED)
                    if (Constants.STATUS_COMPLETED.equalsIgnoreCase(status)) {
                        filtered.add(b);
                    }
                    break;
                case 4: // Đã hủy (CANCELLED & REJECTED)
                    if (Constants.STATUS_CANCELLED.equalsIgnoreCase(status)
                            || Constants.STATUS_REJECTED.equalsIgnoreCase(status)) {
                        filtered.add(b);
                    }
                    break;
            }
        }

        adapter.setBookings(filtered);

        if (filtered.isEmpty()) {
            binding.layoutAdminEmpty.setVisibility(View.VISIBLE);
            binding.rvAdminBookings.setVisibility(View.GONE);
        } else {
            binding.layoutAdminEmpty.setVisibility(View.GONE);
            binding.rvAdminBookings.setVisibility(View.VISIBLE);
        }
    }

    private void showApproveConfirmationDialog(Booking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_confirm_approve_title)
                .setMessage(String.format(getString(R.string.admin_confirm_approve_msg), booking.getId()))
                .setPositiveButton(R.string.admin_btn_approve, (dialog, which) -> {
                    bookingViewModel.updateBookingStatus(booking.getId(), Constants.STATUS_CONFIRMED);
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    private void showRejectConfirmationDialog(Booking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_confirm_reject_title)
                .setMessage(String.format(getString(R.string.admin_confirm_reject_msg), booking.getId()))
                .setPositiveButton(R.string.admin_btn_reject, (dialog, which) -> {
                    bookingViewModel.updateBookingStatus(booking.getId(), Constants.STATUS_REJECTED);
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    private void showCompleteConfirmationDialog(Booking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_confirm_complete_title)
                .setMessage(String.format(getString(R.string.admin_confirm_complete_msg), booking.getId()))
                .setPositiveButton(R.string.admin_btn_complete, (dialog, which) -> {
                    bookingViewModel.updateBookingStatus(booking.getId(), Constants.STATUS_COMPLETED);
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

