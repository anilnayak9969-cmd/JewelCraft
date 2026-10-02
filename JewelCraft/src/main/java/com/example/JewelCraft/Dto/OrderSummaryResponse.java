package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Totals shown on the checkout page before the order is placed. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
	private int totalItems;
	private BigDecimal subtotal;
	private BigDecimal discount;
	private BigDecimal tax;
	private BigDecimal deliveryCharge;
	private BigDecimal total;
	private String couponCode;
	private boolean couponApplied;
	private String couponMessage;
}
