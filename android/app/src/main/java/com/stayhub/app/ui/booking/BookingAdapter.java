package com.stayhub.app.ui.booking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhub.app.R;
import com.stayhub.app.data.model.Booking;
import com.stayhub.app.databinding.ItemBookingBinding;
import com.stayhub.app.util.Constants;
import com.stayhub.app.util.CurrencyUtils;
import com.stayhub.app.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter danh sách đơn đặt phòng tại MyBookingsFragment
 */
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onCancelBooking(Booking booking);
        void onReviewBooking(Booking booking);
    }

    private final List<Booking> bookings = new ArrayList<>();
    private final OnBookingActionListener actionListener;

    public BookingAdapter(OnBookingActionListener actionListener) {
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
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingBinding binding = ItemBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {

        private final ItemBookingBinding binding;

        public BookingViewHolder(@NonNull ItemBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Booking booking) {
            Context context = binding.getRoot().getContext();

            binding.tvBookingCode.setText(String.format(context.getString(R.string.booking_code), booking.getId()));
            binding.tvHomestayName.setText(booking.getHomestayName());
            binding.tvRoomName.setText(String.format("%s • %d khách", booking.getRoomName(), booking.getGuests()));

            // Định dạng ngày nhận - trả phòng và tính số đêm
            int nights = DateUtils.calculateNights(booking.getCheckIn(), booking.getCheckOut());
            binding.tvBookingDates.setText(String.format(
                    context.getString(R.string.booking_dates_format),
                    DateUtils.toDisplayDate(booking.getCheckIn()),
                    DateUtils.toDisplayDate(booking.getCheckOut()),
                    nights
            ));

            binding.tvTotalPrice.setText(CurrencyUtils.formatVnd(booking.getTotalPrice()));

            // Cấu hình màu sắc & chữ hiển thị cho Trạng thái Booking
            bindStatus(context, booking.getStatus());

            // Nút "Hủy đơn" chỉ hiển thị khi đơn đang PENDING hoặc CONFIRMED
            if (booking.canCancel()) {
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
                binding.btnCancelBooking.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onCancelBooking(booking);
                    }
                });
            } else {
                binding.btnCancelBooking.setVisibility(View.GONE);
            }

            // Nút "Đánh giá" chỉ hiển thị khi đơn đã COMPLETED và chưa từng được đánh giá
            if (booking.canReview()) {
                binding.btnReviewBooking.setVisibility(View.VISIBLE);
                binding.btnReviewBooking.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onReviewBooking(booking);
                    }
                });
            } else {
                binding.btnReviewBooking.setVisibility(View.GONE);
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

            binding.tvBookingStatus.setText(text);
            binding.tvBookingStatus.setTextColor(textColor);
            binding.tvBookingStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, bgColor));
        }
    }
}
