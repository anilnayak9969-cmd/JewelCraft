package com.example.JewelCraft.Services;

import com.example.JewelCraft.Dto.CartItemRequest;
import com.example.JewelCraft.Dto.CartResponse;

public interface CartService {

	CartResponse getCart(Long userId);

	CartResponse addItem(Long userId, CartItemRequest request);

	CartResponse updateItem(Long userId, Long itemId, int quantity);

	CartResponse removeItem(Long userId, Long itemId);

	CartResponse clearCart(Long userId);
}
