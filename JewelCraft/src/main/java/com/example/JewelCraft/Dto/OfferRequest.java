package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OfferRequest {

	@NotBlank(message = "Coupon code is required")
	@Pattern(regexp = "^[A-Za-z0-9_-]{3,30}$", message = "Code must be 3-30 letters, numbers, - or _")
	private String code;

	@NotBlank(message = "Title is required")
	@Size(max = 100)
	private String title;

	@Size(max = 500)
	private String description;

	@NotNull(message = "Discount percent is required")
	@DecimalMin(value = "0.01", message = "Discount must be greater than 0")
	@DecimalMax(value = "90", message = "Discount cannot exceed 90%")
	private BigDecimal discountPercent;

	@DecimalMin(value = "0", message = "Max discount cannot be negative")
	private BigDecimal maxDiscountAmount;

	@DecimalMin(value = "0", message = "Minimum order cannot be negative")
	private BigDecimal minOrderAmount;

	@NotNull(message = "Start date is required")
	private LocalDate startDate;

	@NotNull(message = "End date is required")
	private LocalDate endDate;

	private Boolean active;
}
