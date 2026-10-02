package com.example.JewelCraft.Services;

import java.math.BigDecimal;
import java.util.List;

import com.example.JewelCraft.Dto.OrderTotals;
import com.example.JewelCraft.Dto.PriceBreakdown;
import com.example.JewelCraft.Dto.PricingLine;
import com.example.JewelCraft.Entites.Offer;
import com.example.JewelCraft.Entites.Product;

public interface PricingService {

	PriceBreakdown calculate(BigDecimal weight, BigDecimal metalPricePerGram, BigDecimal makingCharges,
			BigDecimal stoneCharges, BigDecimal discount, BigDecimal taxPercent);

	PriceBreakdown breakdown(Product product);

	/** Sets product.metalPrice to the given rate and recalculates product.finalPrice. */
	void applyPrice(Product product, BigDecimal metalPricePerGram);

	/** Checkout totals. offer may be null. */
	OrderTotals calculateTotals(List<PricingLine> lines, Offer offer);

	/** Coupon discount on a pre-tax subtotal, respecting the offer's max cap. */
	BigDecimal couponDiscount(BigDecimal subtotal, Offer offer);
}
