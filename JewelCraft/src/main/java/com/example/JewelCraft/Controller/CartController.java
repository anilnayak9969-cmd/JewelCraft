package com.example.JewelCraft.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.CartItemRequest;
import com.example.JewelCraft.Dto.CartResponse;
import com.example.JewelCraft.Dto.QuantityRequest;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** CUSTOMER only (enforced in WebSecurityConfig). */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@GetMapping
	public CartResponse getCart(@AuthenticationPrincipal UserDetailsImpl user) {
		return cartService.getCart(user.getId());
	}

	@PostMapping("/items")
	public CartResponse addItem(@AuthenticationPrincipal UserDetailsImpl user,
			@Valid @RequestBody CartItemRequest request) {
		return cartService.addItem(user.getId(), request);
	}

	@PutMapping("/items/{id}")
	public CartResponse updateItem(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
			@Valid @RequestBody QuantityRequest request) {
		return cartService.updateItem(user.getId(), id, request.getQuantity());
	}

	@DeleteMapping("/items/{id}")
	public CartResponse removeItem(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
		return cartService.removeItem(user.getId(), id);
	}

	@DeleteMapping("/clear")
	public CartResponse clearCart(@AuthenticationPrincipal UserDetailsImpl user) {
		return cartService.clearCart(user.getId());
	}
}
