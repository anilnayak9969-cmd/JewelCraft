package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

/** One cart/order line as seen by the pricing service: price before tax, tax %, quantity. */
public record PricingLine(BigDecimal unitPrice, BigDecimal taxRate, int quantity) {
}
