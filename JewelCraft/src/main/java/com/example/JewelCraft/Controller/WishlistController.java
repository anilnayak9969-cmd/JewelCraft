package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.WishlistItemResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.WishlistService;

import lombok.RequiredArgsConstructor;

/** CUSTOMER only (enforced in WebSecurityConfig). */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

	private final WishlistService wishlistService;

	@GetMapping
	public List<WishlistItemResponse> getWishlist(@AuthenticationPrincipal UserDetailsImpl user) {
		return wishlistService.getWishlist(user.getId());
	}

	@PostMapping("/{productId}")
	public List<WishlistItemResponse> addProduct(@AuthenticationPrincipal UserDetailsImpl user,
			@PathVariable Long productId) {
		return wishlistService.addProduct(user.getId(), productId);
	}

	@DeleteMapping("/{productId}")
	public List<WishlistItemResponse> removeProduct(@AuthenticationPrincipal UserDetailsImpl user,
			@PathVariable Long productId) {
		return wishlistService.removeProduct(user.getId(), productId);
	}
}
