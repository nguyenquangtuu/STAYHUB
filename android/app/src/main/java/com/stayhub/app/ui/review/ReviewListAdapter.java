package com.stayhub.app.ui.review;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhub.app.data.model.Review;
import com.stayhub.app.databinding.ItemReviewBinding;
import com.stayhub.app.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter danh sách đánh giá của Homestay
 */
public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private final List<Review> reviews = new ArrayList<>();

    public void setReviews(List<Review> newReviews) {
        this.reviews.clear();
        if (newReviews != null) {
            this.reviews.addAll(newReviews);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding binding = ItemReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final ItemReviewBinding binding;

        public ReviewViewHolder(@NonNull ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Review review) {
            binding.tvUserName.setText(review.getUserName());
            binding.tvRating.setText(String.format(Locale.getDefault(), "%d", review.getRating()));
            binding.tvDate.setText(DateUtils.toDisplayDate(review.getCreatedAt()));
            binding.tvComment.setText(review.getComment() != null ? review.getComment() : "");
        }
    }
}
