package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class QuantityRequest {

	@Min(value = 1, message = "Quantity must be at least 1")
	@Max(value = 10, message = "You can order at most 10 of an item")
	private int quantity;
}
