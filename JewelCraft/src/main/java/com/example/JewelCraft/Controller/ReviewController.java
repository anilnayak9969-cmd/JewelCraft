package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.ReviewRequest;
import com.example.JewelCraft.Dto.ReviewResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Listing and creating reviews for a product lives in ProductController (/api/products/{id}/reviews). */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	/** Public: recent 4-5 star reviews for the home page. */
	@GetMapping("/latest")
	public List<ReviewResponse> getLatest() {
		return reviewService.getLatestReviews();
	}

	@GetMapping("/my")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<ReviewResponse> getMyReviews(@AuthenticationPrincipal UserDetailsImpl user) {
		return reviewService.getMyReviews(user.getId());
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ReviewResponse updateReview(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
			@Valid @RequestBody ReviewRequest request) {
		return reviewService.updateReview(user.getId(), id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
	public void deleteReview(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
		reviewService.deleteReview(user, id);
	}
}
