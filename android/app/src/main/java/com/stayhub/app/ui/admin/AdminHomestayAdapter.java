package com.stayhub.app.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stayhub.app.R;
import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.databinding.ItemAdminHomestayBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách Homestay phục vụ quản trị (Sửa / Xóa)
 */
public class AdminHomestayAdapter extends RecyclerView.Adapter<AdminHomestayAdapter.ViewHolder> {

    public interface OnHomestayActionListener {
        void onEdit(Homestay homestay);
        void onDelete(Homestay homestay);
    }

    private final List<Homestay> homestays = new ArrayList<>();
    private final OnHomestayActionListener listener;

    public AdminHomestayAdapter(OnHomestayActionListener listener) {
        this.listener = listener;
    }

    public void setHomestays(List<Homestay> newHomestays) {
        homestays.clear();
        if (newHomestays != null) {
            homestays.addAll(newHomestays);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminHomestayBinding binding = ItemAdminHomestayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(homestays.get(position));
    }

    @Override
    public int getItemCount() {
        return homestays.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminHomestayBinding binding;

        ViewHolder(ItemAdminHomestayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Homestay homestay) {
            binding.tvHomestayId.setText("ID #" + homestay.getId());
            binding.tvHomestayName.setText(homestay.getName());
            binding.tvHomestayRating.setText(String.format(java.util.Locale.US, "%.1f", homestay.getAvgRating()));
            binding.tvHomestayLocation.setText(homestay.getAddress() + ", " + homestay.getCity());

            Glide.with(itemView.getContext())
                    .load(homestay.getImageUrl())
                    .placeholder(R.drawable.bg_image_placeholder)
                    .error(R.drawable.bg_image_placeholder)
                    .centerCrop()
                    .into(binding.ivHomestayThumb);

            if (homestay.getDescription() != null && !homestay.getDescription().isEmpty()) {
                binding.tvHomestayDesc.setText(homestay.getDescription());
                binding.tvHomestayDesc.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvHomestayDesc.setVisibility(android.view.View.GONE);
            }

            binding.btnEditHomestay.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(homestay);
            });

            binding.btnDeleteHomestay.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(homestay);
            });
        }
    }
}

