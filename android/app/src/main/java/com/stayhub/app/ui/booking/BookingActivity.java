package com.stayhub.app.ui.booking;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.databinding.ActivityBookingBinding;
import com.stayhub.app.util.Constants;
import com.stayhub.app.util.CurrencyUtils;
import com.stayhub.app.util.DateUtils;
import com.stayhub.app.viewmodel.BookingViewModel;

import java.util.Calendar;

/**
 * Màn hình Đặt phòng: Chọn ngày, số khách, kiểm tra phòng trống và xác nhận đặt
 */
public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private BookingViewModel bookingViewModel;

    private int roomId;
    private String roomName;
    private double pricePerNight;
    private int capacity;
    private String homestayName;

    private String selectedCheckInDate;
    private String selectedCheckOutDate;
    private boolean isRoomAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        extractIntentData();
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        setupToolbar();
        setupInitialData();
        setupDatePickers();
        setupListeners();
        observeViewModel();

        // Kiểm tra phòng trống với ngày mặc định (hôm nay -> ngày mai)
        triggerAvailabilityCheck();
    }

    private void extractIntentData() {
        roomId = getIntent().getIntExtra(Constants.EXTRA_ROOM_ID, -1);
        roomName = getIntent().getStringExtra(Constants.EXTRA_ROOM_NAME);
        pricePerNight = getIntent().getDoubleExtra(Constants.EXTRA_ROOM_PRICE, 0.0);
        capacity = getIntent().getIntExtra(Constants.EXTRA_ROOM_CAPACITY, 2);
        homestayName = getIntent().getStringExtra(Constants.EXTRA_HOMESTAY_NAME);

        if (roomId == -1) {
            finish();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupInitialData() {
        binding.tvBookingHomestayName.setText(homestayName != null ? homestayName : "Homestay");
        binding.tvBookingRoomName.setText(roomName != null ? roomName : "Phòng");
        binding.tvPricePerNight.setText(String.format(
                getString(R.string.price_range_format),
                CurrencyUtils.formatVnd(pricePerNight)
        ));

        // Mặc định: Check-in là ngày mai, check-out là 2 ngày sau
        selectedCheckInDate = DateUtils.getTomorrowApiDate();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 3);
        selectedCheckOutDate = DateUtils.toApiDate(cal.getTime());

        binding.etCheckIn.setText(DateUtils.toDisplayDate(selectedCheckInDate));
        binding.etCheckOut.setText(DateUtils.toDisplayDate(selectedCheckOutDate));

        updateCalculation();
    }

    private void setupDatePickers() {
        binding.etCheckIn.setOnClickListener(v -> showDatePicker(true));
        binding.tilCheckIn.setEndIconOnClickListener(v -> showDatePicker(true));

        binding.etCheckOut.setOnClickListener(v -> showDatePicker(false));
        binding.tilCheckOut.setEndIconOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar pickedCal = Calendar.getInstance();
                    pickedCal.set(year, month, dayOfMonth);

                    if (isCheckIn) {
                        selectedCheckInDate = DateUtils.toApiDate(pickedCal.getTime());
                        binding.etCheckIn.setText(DateUtils.toDisplayDate(selectedCheckInDate));
                    } else {
                        selectedCheckOutDate = DateUtils.toApiDate(pickedCal.getTime());
                        binding.etCheckOut.setText(DateUtils.toDisplayDate(selectedCheckOutDate));
                    }

                    updateCalculation();
                    triggerAvailabilityCheck();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        // Không cho phép chọn ngày trong quá khứ
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void setupListeners() {
        binding.etGuests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                validateGuests();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnConfirmBooking.setOnClickListener(v -> performBooking());
    }

    private boolean validateGuests() {
        String guestsStr = binding.etGuests.getText() != null ? binding.etGuests.getText().toString().trim() : "1";
        try {
            int guests = Integer.parseInt(guestsStr);
            if (guests <= 0) {
                binding.tilGuests.setError("Số khách tối thiểu là 1");
                return false;
            }
            if (guests > capacity) {
                binding.tilGuests.setError(String.format(getString(R.string.error_exceed_capacity), capacity));
                return false;
            }
            binding.tilGuests.setError(null);
            return true;
        } catch (NumberFormatException e) {
            binding.tilGuests.setError("Vui lòng nhập số hợp lệ");
            return false;
        }
    }

    private void updateCalculation() {
        int nights = DateUtils.calculateNights(selectedCheckInDate, selectedCheckOutDate);
        if (nights <= 0) {
            binding.tvNightsCount.setText("0 đêm (Không hợp lệ)");
            binding.tvTotalPrice.setText("0 ₫");
            binding.btnConfirmBooking.setEnabled(false);
            return;
        }

        binding.tvNightsCount.setText(String.format(getString(R.string.nights_count), nights));
        double total = nights * pricePerNight;
        binding.tvTotalPrice.setText(CurrencyUtils.formatVnd(total));
    }

    /**
     * Gọi API GET /api/rooms/:id/availability khi người dùng thay đổi ngày
     */
    private void triggerAvailabilityCheck() {
        int nights = DateUtils.calculateNights(selectedCheckInDate, selectedCheckOutDate);
        if (nights <= 0) {
            binding.layoutAvailabilityStatus.setVisibility(View.VISIBLE);
            binding.imgAvailabilityIcon.setImageResource(R.drawable.ic_error);
            binding.imgAvailabilityIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.error)));
            binding.tvAvailabilityStatus.setText(getString(R.string.error_invalid_dates));
            binding.tvAvailabilityStatus.setTextColor(getColor(R.color.error));
            binding.btnConfirmBooking.setEnabled(false);
            return;
        }

        binding.layoutAvailabilityStatus.setVisibility(View.VISIBLE);
        binding.progressAvailability.setVisibility(View.VISIBLE);
        binding.tvAvailabilityStatus.setText(getString(R.string.check_availability_checking));
        binding.tvAvailabilityStatus.setTextColor(getColor(R.color.text_secondary));

        bookingViewModel.checkAvailability(roomId, selectedCheckInDate, selectedCheckOutDate);
    }

    /**
     * Gọi API POST /api/bookings xác nhận đặt phòng
     */
    private void performBooking() {
        if (!validateGuests()) return;

        if (!isRoomAvailable) {
            Toast.makeText(this, getString(R.string.room_not_available), Toast.LENGTH_SHORT).show();
            return;
        }

        int guests = 1;
        try {
            guests = Integer.parseInt(binding.etGuests.getText().toString().trim());
        } catch (Exception ignored) {}

        String note = binding.etNote.getText() != null ? binding.etNote.getText().toString().trim() : null;

        bookingViewModel.createBooking(roomId, selectedCheckInDate, selectedCheckOutDate, guests, note);
    }

    private void observeViewModel() {
        // Trạng thái kiểm tra phòng trống
        bookingViewModel.getAvailabilityState().observe(this, state -> {
            if (state == null) return;
            binding.progressAvailability.setVisibility(View.GONE);

            if (state.isSuccess() && state.getData() != null) {
                isRoomAvailable = state.getData().isAvailable();
                if (isRoomAvailable) {
                    binding.imgAvailabilityIcon.setImageResource(R.drawable.ic_check_circle);
                    binding.imgAvailabilityIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.success)));
                    binding.tvAvailabilityStatus.setText(getString(R.string.room_is_available));
                    binding.tvAvailabilityStatus.setTextColor(getColor(R.color.success));
                    binding.btnConfirmBooking.setEnabled(true);
                } else {
                    binding.imgAvailabilityIcon.setImageResource(R.drawable.ic_error);
                    binding.imgAvailabilityIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.error)));
                    binding.tvAvailabilityStatus.setText(state.getData().getReason() != null
                            ? state.getData().getReason()
                            : getString(R.string.room_not_available));
                    binding.tvAvailabilityStatus.setTextColor(getColor(R.color.error));
                    binding.btnConfirmBooking.setEnabled(false);

                    // Cảnh báo Toast theo yêu cầu mục III.6
                    Toast.makeText(this, getString(R.string.room_not_available), Toast.LENGTH_SHORT).show();
                }
            } else if (state.isError()) {
                isRoomAvailable = false;
                binding.imgAvailabilityIcon.setImageResource(R.drawable.ic_error);
                binding.imgAvailabilityIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.error)));
                binding.tvAvailabilityStatus.setText(state.getMessage());
                binding.tvAvailabilityStatus.setTextColor(getColor(R.color.error));
                binding.btnConfirmBooking.setEnabled(false);
            }
        });

        // Trạng thái tạo đơn đặt phòng
        bookingViewModel.getCreateBookingState().observe(this, state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    binding.progressBarBooking.setVisibility(View.VISIBLE);
                    binding.btnConfirmBooking.setEnabled(false);
                    binding.btnConfirmBooking.setText("");
                    break;

                case SUCCESS:
                    binding.progressBarBooking.setVisibility(View.GONE);
                    Toast.makeText(this, "Đặt phòng thành công! Đơn của bạn đang chờ xác nhận", Toast.LENGTH_LONG).show();
                    finish();
                    break;

                case ERROR:
                    binding.progressBarBooking.setVisibility(View.GONE);
                    binding.btnConfirmBooking.setEnabled(true);
                    binding.btnConfirmBooking.setText(getString(R.string.btn_confirm_booking));
                    String message = state.getMessage() != null ? state.getMessage() : getString(R.string.error_network);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }
}

