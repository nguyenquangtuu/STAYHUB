package com.stayhub.app.ui.admin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhub.app.R;
import com.stayhub.app.data.model.Booking;
import com.stayhub.app.databinding.ItemAdminBookingBinding;
import com.stayhub.app.util.Constants;
import com.stayhub.app.util.CurrencyUtils;
import com.stayhub.app.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter danh sách đơn đặt phòng dành cho Admin quản trị hệ thống
 */
public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.AdminBookingViewHolder> {

    public interface OnAdminActionListener {
        void onApprove(Booking booking);
        void onReject(Booking booking);
        void onComplete(Booking booking);
    }

    private final List<Booking> bookings = new ArrayList<>();
    private final OnAdminActionListener actionListener;

    public AdminBookingAdapter(OnAdminActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setBookings(List<Booking> newBookings) {
        this.bookings.clear();
        if (newBookings != null) {
            this.bookings.addAll(newBookings);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminBookingBinding binding = ItemAdminBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AdminBookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookingViewHolder holder, int position) {
        holder.bind(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class AdminBookingViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminBookingBinding binding;

        public AdminBookingViewHolder(@NonNull ItemAdminBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Booking booking) {
            Context context = binding.getRoot().getContext();

            binding.tvAdminBookingCode.setText(String.format("Mã đơn: #%d", booking.getId()));
            binding.tvAdminHomestayName.setText(booking.getHomestayName());
            binding.tvAdminRoomName.setText(String.format("%s • %d khách", booking.getRoomName(), booking.getGuests()));

            // Khách hàng
            binding.tvAdminCustomerName.setText(booking.getUserName());
            String phone = booking.getUserPhone() != null ? booking.getUserPhone() : "Chưa có SĐT";
            String email = booking.getUserEmail() != null ? booking.getUserEmail() : "";
            binding.tvAdminCustomerContact.setText(String.format("%s • %s", phone, email));

            // Ngày nhận - trả phòng và số đêm
            int nights = DateUtils.calculateNights(booking.getCheckIn(), booking.getCheckOut());
            binding.tvAdminBookingDates.setText(String.format(
                    context.getString(R.string.booking_dates_format),
                    DateUtils.toDisplayDate(booking.getCheckIn()),
                    DateUtils.toDisplayDate(booking.getCheckOut()),
                    nights
            ));

            binding.tvAdminTotalPrice.setText(CurrencyUtils.formatVnd(booking.getTotalPrice()));

            // Trạng thái đơn
            bindStatus(context, booking.getStatus());

            // Nút thao tác Admin
            if (Constants.STATUS_PENDING.equalsIgnoreCase(booking.getStatus())) {
                binding.layoutAdminPendingActions.setVisibility(View.VISIBLE);
                binding.layoutAdminConfirmedActions.setVisibility(View.GONE);

                binding.btnAdminApprove.setOnClickListener(v -> {
                    if (actionListener != null) actionListener.onApprove(booking);
                });
                binding.btnAdminReject.setOnClickListener(v -> {
                    if (actionListener != null) actionListener.onReject(booking);
                });
            } else if (Constants.STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())) {
                binding.layoutAdminPendingActions.setVisibility(View.GONE);
                binding.layoutAdminConfirmedActions.setVisibility(View.VISIBLE);

                binding.btnAdminComplete.setOnClickListener(v -> {
                    if (actionListener != null) actionListener.onComplete(booking);
                });
            } else {
                binding.layoutAdminPendingActions.setVisibility(View.GONE);
                binding.layoutAdminConfirmedActions.setVisibility(View.GONE);
            }
        }

        private void bindStatus(Context context, String status) {
            int textColor;
            int bgColor;
            String text;

            if (Constants.STATUS_CONFIRMED.equalsIgnoreCase(status)) {
                textColor = ContextCompat.getColor(context, R.color.status_confirmed);
                bgColor = ContextCompat.getColor(context, R.color.status_confirmed_bg);
                text = context.getString(R.string.status_confirmed_label);
            } else if (Constants.STATUS_COMPLETED.equalsIgnoreCase(status)) {
                textColor = ContextCompat.getColor(context, R.color.status_completed);
                bgColor = ContextCompat.getColor(context, R.color.status_completed_bg);
                text = context.getString(R.string.status_completed_label);
            } else if (Constants.STATUS_CANCELLED.equalsIgnoreCase(status)) {
                textColor = ContextCompat.getColor(context, R.color.status_cancelled);
                bgColor = ContextCompat.getColor(context, R.color.status_cancelled_bg);
                text = context.getString(R.string.status_cancelled_label);
            } else if (Constants.STATUS_REJECTED.equalsIgnoreCase(status)) {
                textColor = ContextCompat.getColor(context, R.color.status_rejected);
                bgColor = ContextCompat.getColor(context, R.color.status_rejected_bg);
                text = context.getString(R.string.status_rejected_label);
            } else { // PENDING
                textColor = ContextCompat.getColor(context, R.color.status_pending);
                bgColor = ContextCompat.getColor(context, R.color.status_pending_bg);
                text = context.getString(R.string.status_pending_label);
            }

            binding.tvAdminBookingStatus.setText(text);
            binding.tvAdminBookingStatus.setTextColor(textColor);
            binding.tvAdminBookingStatus.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        }
    }
}

