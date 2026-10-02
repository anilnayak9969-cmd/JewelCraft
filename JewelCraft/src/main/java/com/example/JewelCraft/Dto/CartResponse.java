package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
	private List<CartItemResponse> items;
	private int totalItems;
	/** Sum before tax. */
	private BigDecimal subtotal;
	private BigDecimal tax;
	private BigDecimal total;
}
