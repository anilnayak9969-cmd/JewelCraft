package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.ReviewRequest;
import com.example.JewelCraft.Dto.ReviewResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;

public interface ReviewService {

	PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size);

	List<ReviewResponse> getMyReviews(Long userId);

	List<ReviewResponse> getLatestReviews();

	ReviewResponse addReview(Long userId, Long productId, ReviewRequest request);

	ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request);

	/** The author or an admin may delete. */
	void deleteReview(UserDetailsImpl user, Long reviewId);
}
