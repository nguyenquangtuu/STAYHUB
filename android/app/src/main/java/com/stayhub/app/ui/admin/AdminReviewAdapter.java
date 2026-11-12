package com.stayhub.app.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhub.app.data.model.Review;
import com.stayhub.app.databinding.ItemAdminReviewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách đánh giá phục vụ kiểm duyệt và xóa vi phạm của Admin
 */
public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ViewHolder> {

    public interface OnReviewActionListener {
        void onDelete(Review review);
    }

    private final List<Review> reviews = new ArrayList<>();
    private final OnReviewActionListener listener;

    public AdminReviewAdapter(OnReviewActionListener listener) {
        this.listener = listener;
    }

    public void setReviews(List<Review> newReviews) {
        reviews.clear();
        if (newReviews != null) {
            reviews.addAll(newReviews);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminReviewBinding binding = ItemAdminReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminReviewBinding binding;

        ViewHolder(ItemAdminReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Review review) {
            String author = review.getUserName() != null ? review.getUserName() : "Người dùng #" + review.getUserId();
            binding.tvAdminReviewAuthor.setText(author);
            binding.tvAdminReviewRating.setText(review.getRating() + " ★");

            String date = review.getCreatedAt() != null ? review.getCreatedAt() : "";
            binding.tvAdminReviewDate.setText("Mã ĐG #" + review.getId() + (date.isEmpty() ? "" : " • " + date));

            binding.tvAdminReviewComment.setText(review.getComment() != null ? review.getComment() : "(Không có nhận xét văn bản)");

            binding.btnDeleteReview.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(review);
            });
        }
    }
}
