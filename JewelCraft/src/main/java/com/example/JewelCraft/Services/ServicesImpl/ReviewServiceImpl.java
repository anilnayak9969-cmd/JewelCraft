package com.example.JewelCraft.Services.ServicesImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.ReviewRequest;
import com.example.JewelCraft.Dto.ReviewResponse;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Entites.Review;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Exception.UnauthorizedException;
import com.example.JewelCraft.Mapper.EngagementMapper;
import com.example.JewelCraft.Repositories.ProductRepository;
import com.example.JewelCraft.Repositories.ReviewRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final EngagementMapper mapper;

	@Override
	@Transactional(readOnly = true)
	public PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size) {
		return PageResponse.of(
				reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, ServiceUtils.page(page, size)),
				mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReviewResponse> getMyReviews(Long userId) {
		return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReviewResponse> getLatestReviews() {
		return reviewRepository.findTop6ByRatingGreaterThanEqualOrderByCreatedAtDesc(4).stream()
				.map(mapper::toResponse).toList();
	}

	@Override
	@Transactional
	public ReviewResponse addReview(Long userId, Long productId, ReviewRequest req) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
		if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
			throw new BadRequestException("You have already reviewed this product. You can edit your review instead.");
		}
		Review review = Review.builder()
				.product(product)
				.user(userRepository.getReferenceById(userId))
				.rating(req.getRating())
				.title(ServiceUtils.trimToNull(req.getTitle()))
				.comment(req.getComment().trim())
				.build();
		reviewRepository.saveAndFlush(review);
		return mapper.toResponse(reviewRepository.findById(review.getId()).orElseThrow());
	}

	@Override
	@Transactional
	public ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest req) {
		Review review = find(reviewId);
		if (!review.getUser().getId().equals(userId)) {
			throw new UnauthorizedException("You can only edit your own reviews");
		}
		review.setRating(req.getRating());
		review.setTitle(ServiceUtils.trimToNull(req.getTitle()));
		review.setComment(req.getComment().trim());
		return mapper.toResponse(review);
	}

	@Override
	@Transactional
	public void deleteReview(UserDetailsImpl user, Long reviewId) {
		Review review = find(reviewId);
		if (!review.getUser().getId().equals(user.getId()) && !user.hasRole(RoleName.ADMIN)) {
			throw new UnauthorizedException("You can only delete your own reviews");
		}
		reviewRepository.delete(review);
	}

	private Review find(Long id) {
		return reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
	}
}
