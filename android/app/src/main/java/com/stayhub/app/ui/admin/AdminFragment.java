package com.stayhub.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.stayhub.app.R;
import com.stayhub.app.data.model.Booking;
import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.data.model.Review;
import com.stayhub.app.data.model.Room;
import com.stayhub.app.data.model.request.HomestayRequest;
import com.stayhub.app.data.model.request.RoomRequest;
import com.stayhub.app.data.model.response.HomestayListResponse;
import com.stayhub.app.data.model.response.ReviewListResponse;
import com.stayhub.app.data.repository.ApiCallback;
import com.stayhub.app.data.repository.HomestayRepository;
import com.stayhub.app.data.repository.ReviewRepository;
import com.stayhub.app.data.repository.RoomRepository;
import com.stayhub.app.databinding.DialogAdminRoomBinding;
import com.stayhub.app.databinding.FragmentAdminBinding;
import com.stayhub.app.util.Constants;
import com.stayhub.app.util.CurrencyUtils;
import com.stayhub.app.viewmodel.BookingViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment Quản trị viên (Admin Hub)
 * Quản lý toàn diện:
 * 1. Đơn đặt phòng (Booking approval & tracking)
 * 2. Quản lý Homestay (CRUD)
 * 3. Quản lý Phòng (CRUD)
 * 4. Quản lý Đánh giá (Moderation / Delete)
 */
public class AdminFragment extends Fragment {

    private enum Section {
        BOOKINGS, HOMESTAYS, ROOMS, REVIEWS
    }

    private FragmentAdminBinding binding;
    private BookingViewModel bookingViewModel;

    // Repositories
    private final HomestayRepository homestayRepository = new HomestayRepository();
    private final RoomRepository roomRepository = new RoomRepository();
    private final ReviewRepository reviewRepository = new ReviewRepository();

    // Adapters
    private AdminBookingAdapter bookingAdapter;
    private AdminHomestayAdapter homestayAdapter;
    private AdminRoomAdapter roomAdapter;
    private AdminReviewAdapter reviewAdapter;

    // State
    private Section currentSection = Section.BOOKINGS;
    private final List<Booking> allAdminBookings = new ArrayList<>();
    private final List<Homestay> homestayList = new ArrayList<>();
    private Homestay selectedHomestay = null;
    private int selectedBookingTabPosition = 0; // 0: Tất cả, 1: PENDING, 2: CONFIRMED, 3: COMPLETED, 4: CANCELLED

    private final ActivityResultLauncher<Intent> editHomestayLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    loadHomestays();
                }
            }
    );

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

        setupAdapters();
        setupAdminDrawerNavigation();
        setupBookingTabLayout();
        setupHomestaySpinner();
        setupFab();
        setupSwipeRefresh();
        observeViewModel();

        loadCurrentSectionData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentSectionData();
    }

    private void setupAdapters() {
        // 1. Booking Adapter
        bookingAdapter = new AdminBookingAdapter(new AdminBookingAdapter.OnAdminActionListener() {
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
        binding.rvAdminBookings.setAdapter(bookingAdapter);

        // 2. Homestay Adapter
        homestayAdapter = new AdminHomestayAdapter(new AdminHomestayAdapter.OnHomestayActionListener() {
            @Override
            public void onEdit(Homestay homestay) {
                openEditHomestayActivity(homestay);
            }

            @Override
            public void onDelete(Homestay homestay) {
                showDeleteHomestayDialog(homestay);
            }
        });
        binding.rvAdminHomestays.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminHomestays.setAdapter(homestayAdapter);

        // 3. Room Adapter
        roomAdapter = new AdminRoomAdapter(new AdminRoomAdapter.OnRoomActionListener() {
            @Override
            public void onEdit(Room room) {
                showAddOrEditRoomDialog(room);
            }

            @Override
            public void onDelete(Room room) {
                showDeleteRoomDialog(room);
            }
        });
        binding.rvAdminRooms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminRooms.setAdapter(roomAdapter);

        // 4. Review Adapter
        reviewAdapter = new AdminReviewAdapter(new AdminReviewAdapter.OnReviewActionListener() {
            @Override
            public void onDelete(Review review) {
                showDeleteReviewDialog(review);
            }
        });
        binding.rvAdminReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminReviews.setAdapter(reviewAdapter);
    }

    private void setupAdminDrawerNavigation() {
        binding.btnOpenAdminMenu.setOnClickListener(v -> {
            if (binding.adminDrawerLayout != null) {
                binding.adminDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.adminNavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_bookings) {
                currentSection = Section.BOOKINGS;
                binding.tvAdminSectionTitle.setText("Quản lý Đơn đặt phòng");
                binding.tvAdminSectionSubtitle.setText("Duyệt và theo dõi đơn toàn hệ thống StayHub");
            } else if (itemId == R.id.nav_admin_branches) {
                currentSection = Section.HOMESTAYS;
                binding.tvAdminSectionTitle.setText("Quản lý Chi nhánh");
                binding.tvAdminSectionSubtitle.setText("Danh sách các cơ sở / chi nhánh của chuỗi StayHub");
            } else if (itemId == R.id.nav_admin_rooms) {
                currentSection = Section.ROOMS;
                binding.tvAdminSectionTitle.setText("Quản lý Phòng nghỉ");
                binding.tvAdminSectionSubtitle.setText("Cơ cấu và bảng giá phòng theo từng chi nhánh");
            } else if (itemId == R.id.nav_admin_reviews) {
                currentSection = Section.REVIEWS;
                binding.tvAdminSectionTitle.setText("Quản lý Đánh giá");
                binding.tvAdminSectionSubtitle.setText("Kiểm duyệt phản hồi và khiếu nại của khách hàng");
            }
            binding.adminDrawerLayout.closeDrawer(GravityCompat.START);
            updateSectionVisibility();
            loadCurrentSectionData();
            return true;
        });
    }

    private void updateSectionVisibility() {
        binding.layoutBookingStatsAndFilters.setVisibility(currentSection == Section.BOOKINGS ? View.VISIBLE : View.GONE);
        binding.layoutHomestaySelector.setVisibility(
                (currentSection == Section.ROOMS || currentSection == Section.REVIEWS) ? View.VISIBLE : View.GONE);

        binding.rvAdminBookings.setVisibility(currentSection == Section.BOOKINGS ? View.VISIBLE : View.GONE);
        binding.rvAdminHomestays.setVisibility(currentSection == Section.HOMESTAYS ? View.VISIBLE : View.GONE);
        binding.rvAdminRooms.setVisibility(currentSection == Section.ROOMS ? View.VISIBLE : View.GONE);
        binding.rvAdminReviews.setVisibility(currentSection == Section.REVIEWS ? View.VISIBLE : View.GONE);

        if (currentSection == Section.HOMESTAYS) {
            binding.fabAdminAdd.setText(R.string.admin_btn_add_homestay);
            binding.fabAdminAdd.show();
        } else if (currentSection == Section.ROOMS) {
            binding.fabAdminAdd.setText(R.string.admin_btn_add_room);
            binding.fabAdminAdd.show();
        } else {
            binding.fabAdminAdd.hide();
        }
    }

    private void setupBookingTabLayout() {
        binding.tabLayoutAdmin.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedBookingTabPosition = tab.getPosition();
                filterAndDisplayBookings();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupHomestaySpinner() {
        binding.spinnerAdminHomestay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < homestayList.size()) {
                    selectedHomestay = homestayList.get(position);
                    if (currentSection == Section.ROOMS) {
                        loadRoomsForSelectedHomestay();
                    } else if (currentSection == Section.REVIEWS) {
                        loadReviewsForSelectedHomestay();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupFab() {
        binding.fabAdminAdd.setOnClickListener(v -> {
            if (currentSection == Section.HOMESTAYS) {
                openEditHomestayActivity(null);
            } else if (currentSection == Section.ROOMS) {
                if (selectedHomestay == null) {
                    Toast.makeText(requireContext(), "Vui lòng chọn hoặc thêm chi nhánh trước!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showAddOrEditRoomDialog(null);
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshAdmin.setColorSchemeColors(
                requireContext().getColor(R.color.primary),
                requireContext().getColor(R.color.secondary)
        );
        binding.swipeRefreshAdmin.setOnRefreshListener(this::loadCurrentSectionData);
    }

    private void loadCurrentSectionData() {
        switch (currentSection) {
            case BOOKINGS:
                bookingViewModel.loadAdminBookings(null, null, 1, 100);
                break;
            case HOMESTAYS:
                loadHomestays();
                break;
            case ROOMS:
            case REVIEWS:
                if (homestayList.isEmpty()) {
                    loadHomestays();
                } else if (selectedHomestay != null) {
                    if (currentSection == Section.ROOMS) {
                        loadRoomsForSelectedHomestay();
                    } else {
                        loadReviewsForSelectedHomestay();
                    }
                }
                break;
        }
    }

    private void observeViewModel() {
        bookingViewModel.getAdminBookingsState().observe(getViewLifecycleOwner(), state -> {
            if (state == null || currentSection != Section.BOOKINGS) return;

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

        bookingViewModel.getUpdateStatusState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    binding.progressAdmin.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressAdmin.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), getString(R.string.admin_action_success), Toast.LENGTH_SHORT).show();
                    loadCurrentSectionData();
                    break;

                case ERROR:
                    binding.progressAdmin.setVisibility(View.GONE);
                    String err = state.getMessage() != null ? state.getMessage() : "Cập nhật thất bại";
                    Snackbar.make(binding.getRoot(), err, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    // --- LOGIC PHÂN HỆ 1: ĐƠN ĐẶT PHÒNG ---
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

    private void filterAndDisplayBookings() {
        if (currentSection != Section.BOOKINGS) return;

        List<Booking> filtered = new ArrayList<>();
        for (Booking b : allAdminBookings) {
            String status = b.getStatus();
            switch (selectedBookingTabPosition) {
                case 0: filtered.add(b); break;
                case 1: if (Constants.STATUS_PENDING.equalsIgnoreCase(status)) filtered.add(b); break;
                case 2: if (Constants.STATUS_CONFIRMED.equalsIgnoreCase(status)) filtered.add(b); break;
                case 3: if (Constants.STATUS_COMPLETED.equalsIgnoreCase(status)) filtered.add(b); break;
                case 4:
                    if (Constants.STATUS_CANCELLED.equalsIgnoreCase(status) || Constants.STATUS_REJECTED.equalsIgnoreCase(status)) {
                        filtered.add(b);
                    }
                    break;
            }
        }

        bookingAdapter.setBookings(filtered);
        showEmptyState(filtered.isEmpty(), getString(R.string.admin_empty_bookings), R.drawable.ic_calendar);
    }

    private void showApproveConfirmationDialog(Booking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_confirm_approve_title)
                .setMessage(String.format(getString(R.string.admin_confirm_approve_msg), booking.getId()))
                .setPositiveButton(R.string.admin_btn_approve, (dialog, which) ->
                        bookingViewModel.updateBookingStatus(booking.getId(), Constants.STATUS_CONFIRMED))
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    private void showRejectConfirmationDialog(Booking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_confirm_reject_title)
                .setMessage(String.format(getString(R.string.admin_confirm_reject_msg), booking.getId()))
                .setPositiveButton(R.string.admin_btn_reject, (dialog, which) ->
                        bookingViewModel.updateBookingStatus(booking.getId(), Constants.STATUS_REJECTED))
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    private void showCompleteConfirmationDialog(Booking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_confirm_complete_title)
                .setMessage(String.format(getString(R.string.admin_confirm_complete_msg), booking.getId()))
                .setPositiveButton(R.string.admin_btn_complete, (dialog, which) ->
                        bookingViewModel.updateBookingStatus(booking.getId(), Constants.STATUS_COMPLETED))
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    // --- LOGIC PHÂN HỆ 2: QUẢN LÝ HOMESTAY ---
    private void loadHomestays() {
        binding.progressAdmin.setVisibility(View.VISIBLE);
        homestayRepository.getHomestays(null, null, null, null, null, 1, 100, new ApiCallback<HomestayListResponse>() {
            @Override
            public void onSuccess(HomestayListResponse data, String message) {
                if (!isAdded()) return;
                binding.progressAdmin.setVisibility(View.GONE);
                binding.swipeRefreshAdmin.setRefreshing(false);
                homestayList.clear();
                if (data != null && data.getHomestays() != null) {
                    homestayList.addAll(data.getHomestays());
                }
                homestayAdapter.setHomestays(homestayList);
                updateSpinnerAdapter();

                if (currentSection == Section.HOMESTAYS) {
                    showEmptyState(homestayList.isEmpty(), getString(R.string.admin_empty_homestays), R.drawable.ic_home);
                } else if (currentSection == Section.ROOMS && selectedHomestay != null) {
                    loadRoomsForSelectedHomestay();
                } else if (currentSection == Section.REVIEWS && selectedHomestay != null) {
                    loadReviewsForSelectedHomestay();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                binding.progressAdmin.setVisibility(View.GONE);
                binding.swipeRefreshAdmin.setRefreshing(false);
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateSpinnerAdapter() {
        if (!isAdded()) return;
        List<String> names = new ArrayList<>();
        for (Homestay h : homestayList) {
            names.add(h.getName() + " (" + h.getCity() + ")");
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, names);
        binding.spinnerAdminHomestay.setAdapter(spinnerAdapter);

        if (!homestayList.isEmpty() && selectedHomestay == null) {
            selectedHomestay = homestayList.get(0);
        }
    }

    private void openEditHomestayActivity(@Nullable Homestay homestay) {
        Intent intent = new Intent(requireContext(), AdminEditHomestayActivity.class);
        if (homestay != null) {
            intent.putExtra(AdminEditHomestayActivity.EXTRA_HOMESTAY, homestay);
        }
        editHomestayLauncher.launch(intent);
    }

    private void showDeleteHomestayDialog(Homestay homestay) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa Chi Nhánh")
                .setMessage("Bạn có chắc chắn muốn xóa chi nhánh \"" + homestay.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    binding.progressAdmin.setVisibility(View.VISIBLE);
                    homestayRepository.deleteHomestay(homestay.getId(), new ApiCallback<Void>() {
                        @Override
                        public void onSuccess(Void data, String message) {
                            Toast.makeText(requireContext(), "Đã xóa chi nhánh", Toast.LENGTH_SHORT).show();
                            loadHomestays();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            binding.progressAdmin.setVisibility(View.GONE);
                            Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    // --- LOGIC PHÂN HỆ 3: QUẢN LÝ PHÒNG ---
    private void loadRoomsForSelectedHomestay() {
        if (selectedHomestay == null) return;
        binding.progressAdmin.setVisibility(View.VISIBLE);
        roomRepository.getRoomsByHomestay(selectedHomestay.getId(), new ApiCallback<List<Room>>() {
            @Override
            public void onSuccess(List<Room> data, String message) {
                if (!isAdded()) return;
                binding.progressAdmin.setVisibility(View.GONE);
                binding.swipeRefreshAdmin.setRefreshing(false);
                roomAdapter.setRooms(data);
                if (currentSection == Section.ROOMS) {
                    showEmptyState(data == null || data.isEmpty(), getString(R.string.admin_empty_rooms), R.drawable.ic_hotel);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                binding.progressAdmin.setVisibility(View.GONE);
                binding.swipeRefreshAdmin.setRefreshing(false);
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showAddOrEditRoomDialog(@Nullable Room room) {
        DialogAdminRoomBinding dialogBinding = DialogAdminRoomBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        boolean isEdit = room != null;
        dialogBinding.tvDialogRoomTitle.setText(isEdit ? "Chỉnh sửa Phòng Nghỉ" : "Thêm Phòng Nghỉ Mới");

        // Spinner trạng thái phòng
        String[] statuses = {"Sẵn sàng (available)", "Đang bảo trì (maintenance)"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, statuses);
        dialogBinding.spinnerRoomStatus.setAdapter(statusAdapter);

        if (isEdit) {
            dialogBinding.etRoomName.setText(room.getName());
            dialogBinding.etRoomPrice.setText(String.valueOf(room.getPricePerNight()));
            dialogBinding.etRoomCapacity.setText(String.valueOf(room.getCapacity()));
            dialogBinding.etRoomBedType.setText(room.getBedType());
            if ("maintenance".equalsIgnoreCase(room.getStatus())) {
                dialogBinding.spinnerRoomStatus.setSelection(1);
            } else {
                dialogBinding.spinnerRoomStatus.setSelection(0);
            }
        }

        dialogBinding.btnCancelRoom.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveRoom.setOnClickListener(v -> {
            String name = dialogBinding.etRoomName.getText() != null ? dialogBinding.etRoomName.getText().toString().trim() : "";
            String priceStr = dialogBinding.etRoomPrice.getText() != null ? dialogBinding.etRoomPrice.getText().toString().trim() : "";
            String capacityStr = dialogBinding.etRoomCapacity.getText() != null ? dialogBinding.etRoomCapacity.getText().toString().trim() : "";
            String bedType = dialogBinding.etRoomBedType.getText() != null ? dialogBinding.etRoomBedType.getText().toString().trim() : "";

            if (TextUtils.isEmpty(name)) {
                dialogBinding.tilRoomName.setError("Vui lòng nhập tên phòng");
                return;
            }
            if (TextUtils.isEmpty(priceStr)) {
                dialogBinding.tilRoomPrice.setError("Vui lòng nhập giá phòng");
                return;
            }
            if (TextUtils.isEmpty(capacityStr)) {
                dialogBinding.tilRoomCapacity.setError("Vui lòng nhập sức chứa");
                return;
            }
            if (TextUtils.isEmpty(bedType)) {
                dialogBinding.tilRoomBedType.setError("Vui lòng nhập loại giường");
                return;
            }

            double price;
            int capacity;
            try {
                price = Double.parseDouble(priceStr);
                capacity = Integer.parseInt(capacityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Giá hoặc sức chứa không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            String status = dialogBinding.spinnerRoomStatus.getSelectedItemPosition() == 1 ? "maintenance" : "available";
            RoomRequest request = new RoomRequest(name, price, capacity, bedType, status);

            binding.progressAdmin.setVisibility(View.VISIBLE);
            dialog.dismiss();

            if (isEdit) {
                roomRepository.updateRoom(room.getId(), request, new ApiCallback<Room>() {
                    @Override
                    public void onSuccess(Room data, String message) {
                        Toast.makeText(requireContext(), "Cập nhật phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadRoomsForSelectedHomestay();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        binding.progressAdmin.setVisibility(View.GONE);
                        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                roomRepository.createRoom(selectedHomestay.getId(), request, new ApiCallback<Room>() {
                    @Override
                    public void onSuccess(Room data, String message) {
                        Toast.makeText(requireContext(), "Thêm phòng mới thành công!", Toast.LENGTH_SHORT).show();
                        loadRoomsForSelectedHomestay();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        binding.progressAdmin.setVisibility(View.GONE);
                        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        dialog.show();
    }

    private void showDeleteRoomDialog(Room room) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa Phòng")
                .setMessage("Bạn có chắc chắn muốn xóa phòng \"" + room.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    binding.progressAdmin.setVisibility(View.VISIBLE);
                    roomRepository.deleteRoom(room.getId(), new ApiCallback<Void>() {
                        @Override
                        public void onSuccess(Void data, String message) {
                            Toast.makeText(requireContext(), "Đã xóa phòng", Toast.LENGTH_SHORT).show();
                            loadRoomsForSelectedHomestay();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            binding.progressAdmin.setVisibility(View.GONE);
                            Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    // --- LOGIC PHÂN HỆ 4: QUẢN LÝ ĐÁNH GIÁ ---
    private void loadReviewsForSelectedHomestay() {
        if (selectedHomestay == null) return;
        binding.progressAdmin.setVisibility(View.VISIBLE);
        reviewRepository.getHomestayReviews(selectedHomestay.getId(), 1, 100, new ApiCallback<ReviewListResponse>() {
            @Override
            public void onSuccess(ReviewListResponse data, String message) {
                if (!isAdded()) return;
                binding.progressAdmin.setVisibility(View.GONE);
                binding.swipeRefreshAdmin.setRefreshing(false);
                List<Review> reviews = (data != null && data.getReviews() != null) ? data.getReviews() : new ArrayList<>();
                reviewAdapter.setReviews(reviews);
                if (currentSection == Section.REVIEWS) {
                    showEmptyState(reviews.isEmpty(), getString(R.string.admin_empty_reviews), R.drawable.ic_star);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                binding.progressAdmin.setVisibility(View.GONE);
                binding.swipeRefreshAdmin.setRefreshing(false);
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showDeleteReviewDialog(Review review) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa Đánh Giá Vi Phạm")
                .setMessage("Bạn có chắc chắn muốn xóa đánh giá của người dùng \"" +
                        (review.getUserName() != null ? review.getUserName() : "Khách") + "\" không?")
                .setPositiveButton("Xóa vi phạm", (dialog, which) -> {
                    binding.progressAdmin.setVisibility(View.VISIBLE);
                    reviewRepository.deleteReview(review.getId(), new ApiCallback<Void>() {
                        @Override
                        public void onSuccess(Void data, String message) {
                            Toast.makeText(requireContext(), "Đã xóa đánh giá vi phạm", Toast.LENGTH_SHORT).show();
                            loadReviewsForSelectedHomestay();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            binding.progressAdmin.setVisibility(View.GONE);
                            Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    private void showEmptyState(boolean isEmpty, String emptyMessage, int iconRes) {
        if (isEmpty) {
            binding.layoutAdminEmpty.setVisibility(View.VISIBLE);
            binding.tvAdminEmpty.setText(emptyMessage);
            binding.ivAdminEmptyIcon.setImageResource(iconRes);
            hideActiveRecyclerView();
        } else {
            binding.layoutAdminEmpty.setVisibility(View.GONE);
            showActiveRecyclerView();
        }
    }

    private void hideActiveRecyclerView() {
        switch (currentSection) {
            case BOOKINGS: binding.rvAdminBookings.setVisibility(View.GONE); break;
            case HOMESTAYS: binding.rvAdminHomestays.setVisibility(View.GONE); break;
            case ROOMS: binding.rvAdminRooms.setVisibility(View.GONE); break;
            case REVIEWS: binding.rvAdminReviews.setVisibility(View.GONE); break;
        }
    }

    private void showActiveRecyclerView() {
        switch (currentSection) {
            case BOOKINGS: binding.rvAdminBookings.setVisibility(View.VISIBLE); break;
            case HOMESTAYS: binding.rvAdminHomestays.setVisibility(View.VISIBLE); break;
            case ROOMS: binding.rvAdminRooms.setVisibility(View.VISIBLE); break;
            case REVIEWS: binding.rvAdminReviews.setVisibility(View.VISIBLE); break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
