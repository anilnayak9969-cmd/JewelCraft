package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryUpdateRequest {

	@NotNull(message = "Quantity is required")
	@Min(value = 0, message = "Stock cannot be negative")
	@Max(value = 100000, message = "Stock looks too large")
	private Integer quantity;

	@Min(value = 0, message = "Threshold cannot be negative")
	@Max(value = 10000)
	private Integer lowStockThreshold;
}
