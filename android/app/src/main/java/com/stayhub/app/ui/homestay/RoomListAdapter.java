package com.stayhub.app.ui.homestay;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhub.app.R;
import com.stayhub.app.data.model.Room;
import com.stayhub.app.databinding.ItemRoomBinding;
import com.stayhub.app.util.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter danh sách phòng trong màn hình Chi tiết Homestay
 */
public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomViewHolder> {

    public interface OnRoomBookClickListener {
        void onBookClick(Room room);
    }

    private final List<Room> rooms = new ArrayList<>();
    private final OnRoomBookClickListener bookClickListener;

    public RoomListAdapter(OnRoomBookClickListener bookClickListener) {
        this.bookClickListener = bookClickListener;
    }

    public void setRooms(List<Room> newRooms) {
        this.rooms.clear();
        if (newRooms != null) {
            this.rooms.addAll(newRooms);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRoomBinding binding = ItemRoomBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.bind(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {

        private final ItemRoomBinding binding;

        public RoomViewHolder(@NonNull ItemRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Room room) {
            binding.tvRoomName.setText(room.getName());
            binding.tvCapacity.setText(String.format(
                    binding.getRoot().getContext().getString(R.string.room_capacity),
                    room.getCapacity()
            ));
            binding.tvBedType.setText(String.format(
                    binding.getRoot().getContext().getString(R.string.room_bed_type),
                    room.getBedType()
            ));
            binding.tvRoomPrice.setText(CurrencyUtils.formatVnd(room.getPricePerNight()));

            boolean isAvailable = room.isAvailable();
            if (isAvailable) {
                binding.tvRoomStatus.setText(binding.getRoot().getContext().getString(R.string.status_room_available));
                binding.tvRoomStatus.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_completed));
                binding.tvRoomStatus.setBackgroundTintList(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.status_completed_bg));
                binding.btnBookRoom.setEnabled(true);
            } else {
                binding.tvRoomStatus.setText(binding.getRoot().getContext().getString(R.string.status_room_maintenance));
                binding.tvRoomStatus.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_cancelled));
                binding.tvRoomStatus.setBackgroundTintList(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.status_cancelled_bg));
                binding.btnBookRoom.setEnabled(false);
            }

            binding.btnBookRoom.setOnClickListener(v -> {
                if (bookClickListener != null && isAvailable) {
                    bookClickListener.onBookClick(room);
                }
            });
        }
    }
}

