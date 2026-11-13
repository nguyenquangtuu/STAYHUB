package com.stayhub.app.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhub.app.R;
import com.stayhub.app.data.model.Room;
import com.stayhub.app.databinding.ItemAdminRoomBinding;
import com.stayhub.app.util.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter quản lý phòng nghỉ trong Homestay (Sửa / Xóa)
 */
public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.ViewHolder> {

    public interface OnRoomActionListener {
        void onEdit(Room room);
        void onDelete(Room room);
    }

    private final List<Room> rooms = new ArrayList<>();
    private final OnRoomActionListener listener;

    public AdminRoomAdapter(OnRoomActionListener listener) {
        this.listener = listener;
    }

    public void setRooms(List<Room> newRooms) {
        rooms.clear();
        if (newRooms != null) {
            rooms.addAll(newRooms);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminRoomBinding binding = ItemAdminRoomBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminRoomBinding binding;

        ViewHolder(ItemAdminRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Room room) {
            binding.tvAdminRoomName.setText(room.getName());
            binding.tvAdminRoomCapacity.setText(room.getCapacity() + " khách");
            binding.tvAdminRoomBed.setText(room.getBedType() != null ? room.getBedType() : "Tiêu chuẩn");
            binding.tvAdminRoomPrice.setText(CurrencyUtils.formatVnd(room.getPricePerNight()) + " / đêm");

            boolean isAvailable = "available".equalsIgnoreCase(room.getStatus());
            binding.tvAdminRoomStatus.setText(isAvailable ? "Sẵn sàng" : "Bảo trì");
            if (isAvailable) {
                binding.tvAdminRoomStatus.getBackground().setTint(ContextCompat.getColor(itemView.getContext(), R.color.status_completed_bg));
                binding.tvAdminRoomStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_completed));
            } else {
                binding.tvAdminRoomStatus.getBackground().setTint(ContextCompat.getColor(itemView.getContext(), R.color.status_cancelled_bg));
                binding.tvAdminRoomStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_cancelled));
            }

            binding.btnEditRoom.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(room);
            });

            binding.btnDeleteRoom.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(room);
            });
        }
    }
}

