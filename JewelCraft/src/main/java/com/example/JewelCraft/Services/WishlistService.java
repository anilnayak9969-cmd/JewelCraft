package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.WishlistItemResponse;

public interface WishlistService {

	List<WishlistItemResponse> getWishlist(Long userId);

	List<WishlistItemResponse> addProduct(Long userId, Long productId);

	List<WishlistItemResponse> removeProduct(Long userId, Long productId);
}
