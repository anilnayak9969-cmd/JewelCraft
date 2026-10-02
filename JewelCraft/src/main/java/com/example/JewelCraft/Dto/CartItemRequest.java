package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {

	@NotNull(message = "Product is required")
	private Long productId;

	@Min(value = 1, message = "Quantity must be at least 1")
	@Max(value = 10, message = "You can order at most 10 of an item")
	private int quantity = 1;
}
