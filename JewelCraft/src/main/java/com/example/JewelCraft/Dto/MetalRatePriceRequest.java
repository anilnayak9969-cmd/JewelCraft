package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record MetalRatePriceRequest(
		@NotNull(message = "Price per gram is required")
		@DecimalMin(value = "0.01", message = "Price per gram must be greater than 0")
		BigDecimal pricePerGram) {
}
