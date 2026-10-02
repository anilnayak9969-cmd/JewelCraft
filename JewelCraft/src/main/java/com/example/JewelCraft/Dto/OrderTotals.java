package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

public record OrderTotals(BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal deliveryCharge,
		BigDecimal total) {
}
