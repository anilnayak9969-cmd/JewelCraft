package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

import com.example.JewelCraft.Enum.MetalType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Create/update a product. There is no price field on purpose: the metal price
 * comes from the current metal rate and the final price is calculated on the server.
 */
@Data
public class ProductRequest {

	@NotBlank(message = "Product name is required")
	@Size(min = 3, max = 150, message = "Product name must be 3-150 characters")
	private String name;

	@Size(max = 40)
	private String sku;

	@Size(max = 5000)
	private String description;

	@NotNull(message = "Category is required")
	private Long categoryId;

	@NotNull(message = "Metal type is required")
	private MetalType metalType;

	@NotBlank(message = "Purity is required")
	@Size(max = 20)
	private String purity;

	@NotNull(message = "Weight is required")
	@DecimalMin(value = "0.001", message = "Weight must be greater than 0")
	@DecimalMax(value = "10000", message = "Weight looks too large")
	private BigDecimal weight;

	@NotNull(message = "Making charges are required")
	@DecimalMin(value = "0", message = "Making charges cannot be negative")
	private BigDecimal makingCharges;

	@DecimalMin(value = "0", message = "Stone charges cannot be negative")
	private BigDecimal stoneCharges;

	@DecimalMin(value = "0", message = "Discount cannot be negative")
	private BigDecimal discount;

	@DecimalMin(value = "0", message = "Tax cannot be negative")
	@DecimalMax(value = "50", message = "Tax cannot exceed 50%")
	private BigDecimal tax;

	/** Initial stock - only used when creating a product. */
	@Min(value = 0, message = "Stock cannot be negative")
	@Max(value = 100000)
	private Integer stockQuantity;

	@Min(value = 0)
	private Integer lowStockThreshold;

	private Boolean featured;

	private Boolean active;
}
