package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Price-related fields a store manager can change from the Price Management screen. */
@Data
public class PricingRequest {

	@NotNull(message = "Weight is required")
	@DecimalMin(value = "0.001", message = "Weight must be greater than 0")
	private BigDecimal weight;

	@NotNull(message = "Making charges are required")
	@DecimalMin(value = "0", message = "Making charges cannot be negative")
	private BigDecimal makingCharges;

	@NotNull(message = "Stone charges are required")
	@DecimalMin(value = "0", message = "Stone charges cannot be negative")
	private BigDecimal stoneCharges;

	@NotNull(message = "Discount is required")
	@DecimalMin(value = "0", message = "Discount cannot be negative")
	private BigDecimal discount;

	@NotNull(message = "Tax is required")
	@DecimalMin(value = "0", message = "Tax cannot be negative")
	@DecimalMax(value = "50", message = "Tax cannot exceed 50%")
	private BigDecimal tax;
}
