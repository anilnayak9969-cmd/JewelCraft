package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
	private Long id;
	private Long productId;
	private String productName;
	private String imageUrl;
	private String metalDetails;
	/** Unit price before tax. */
	private BigDecimal unitPrice;
	/** Unit price including tax (what the product card shows). */
	private BigDecimal finalPrice;
	private int quantity;
	/** finalPrice x quantity. */
	private BigDecimal lineTotal;
	private int stockQuantity;
	/** False if the product was deactivated or there is not enough stock. */
	private boolean available;
}
