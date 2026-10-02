package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.JewelCraft.Enum.MetalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
	private Long id;
	private String name;
	private String sku;
	private String description;
	private Long categoryId;
	private String categoryName;
	private MetalType metalType;
	private String purity;
	private BigDecimal weight;

	// Price breakdown
	private BigDecimal metalPrice;
	private BigDecimal metalValue;
	private BigDecimal makingCharges;
	private BigDecimal stoneCharges;
	private BigDecimal subtotal;
	private BigDecimal discount;
	/** Price before tax, after discount. */
	private BigDecimal discountedAmount;
	/** Tax percentage. */
	private BigDecimal tax;
	private BigDecimal taxAmount;
	private BigDecimal finalPrice;

	private int stockQuantity;
	private int lowStockThreshold;
	private boolean inStock;
	private String imageUrl;
	/** Only filled on the product details endpoint. */
	private List<ProductImageResponse> images;
	private boolean featured;
	private boolean active;
	private Double averageRating;
	private Long reviewCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
