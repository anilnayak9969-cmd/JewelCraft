package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

/**
 * Result of the jewellery price formula:
 * metalValue = weight x metalPrice
 * subtotal = metalValue + making + stone
 * discountedAmount = subtotal - discount
 * taxAmount = discountedAmount x tax%
 * finalPrice = discountedAmount + taxAmount
 */
public record PriceBreakdown(BigDecimal metalValue, BigDecimal subtotal, BigDecimal discount,
		BigDecimal discountedAmount, BigDecimal taxAmount, BigDecimal finalPrice) {
}
