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
public class OrderItemResponse {
	private Long id;
	private Long productId;
	private String productName;
	private String productImage;
	private String metalDetails;
	private BigDecimal unitPrice;
	private BigDecimal taxRate;
	private int quantity;
	private BigDecimal lineTotal;
}
