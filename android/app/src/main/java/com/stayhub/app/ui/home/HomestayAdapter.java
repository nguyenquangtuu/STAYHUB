package com.stayhub.app.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.stayhub.app.R;
import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.databinding.ItemHomestayBinding;
import com.stayhub.app.util.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hiển thị danh sách Homestay tại màn hình HomeFragment
 */
public class HomestayAdapter extends RecyclerView.Adapter<HomestayAdapter.HomestayViewHolder> {

    public interface OnHomestayClickListener {
        void onHomestayClick(Homestay homestay);
    }

    private final List<Homestay> homestays = new ArrayList<>();
    private final OnHomestayClickListener listener;

    public HomestayAdapter(OnHomestayClickListener listener) {
        this.listener = listener;
    }

    public void setHomestays(List<Homestay> newHomestays) {
        this.homestays.clear();
        if (newHomestays != null) {
            this.homestays.addAll(newHomestays);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomestayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomestayBinding binding = ItemHomestayBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new HomestayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomestayViewHolder holder, int position) {
        holder.bind(homestays.get(position));
    }

    @Override
    public int getItemCount() {
        return homestays.size();
    }

    class HomestayViewHolder extends RecyclerView.ViewHolder {

        private final ItemHomestayBinding binding;

        public HomestayViewHolder(@NonNull ItemHomestayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Homestay homestay) {
            binding.tvHomestayName.setText(homestay.getName());
            binding.tvLocation.setText(String.format("%s, %s", homestay.getAddress(), homestay.getCity()));

            // Điểm đánh giá
            if (homestay.getAvgRating() > 0) {
                binding.tvRating.setText(String.format(Locale.getDefault(), "%.1f", homestay.getAvgRating()));
            } else {
                binding.tvRating.setText("Mới");
            }

            // Giá thấp nhất
            if (homestay.getMinRoomPrice() > 0) {
                binding.tvMinPrice.setText(String.format(
                        binding.getRoot().getContext().getString(R.string.price_range_format),
                        CurrencyUtils.formatVnd(homestay.getMinRoomPrice())
                ));
            } else {
                binding.tvMinPrice.setText("Liên hệ giá");
            }

            // Tổng số phòng
            if (homestay.getTotalRooms() > 0) {
                binding.tvTotalRooms.setText(String.format(Locale.getDefault(), "%d phòng", homestay.getTotalRooms()));
            } else {
                binding.tvTotalRooms.setText("");
            }

            // Tải ảnh qua Glide với hiệu ứng bo góc
            Glide.with(binding.getRoot().getContext())
                    .load(homestay.getImageUrl())
                    .placeholder(R.drawable.bg_image_placeholder)
                    .error(R.drawable.bg_image_placeholder)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .into(binding.imgHomestay);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onHomestayClick(homestay);
                }
            });
        }
    }
}
