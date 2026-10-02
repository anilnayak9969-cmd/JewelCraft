package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.JewelCraft.Dto.OrderTotals;
import com.example.JewelCraft.Dto.PriceBreakdown;
import com.example.JewelCraft.Dto.PricingLine;
import com.example.JewelCraft.Entites.Offer;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Services.PricingService;

/**
 * All money maths lives here so the React app never decides a price.
 * Amounts are rounded to 2 decimals (paise) with HALF_UP.
 */
@Service
public class PricingServiceImpl implements PricingService {

	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	private final BigDecimal deliveryCharge;
	private final BigDecimal freeDeliveryAbove;

	public PricingServiceImpl(@Value("${app.delivery.charge}") BigDecimal deliveryCharge,
			@Value("${app.delivery.free-above}") BigDecimal freeDeliveryAbove) {
		this.deliveryCharge = deliveryCharge;
		this.freeDeliveryAbove = freeDeliveryAbove;
	}

	@Override
	public PriceBreakdown calculate(BigDecimal weight, BigDecimal metalPricePerGram, BigDecimal makingCharges,
			BigDecimal stoneCharges, BigDecimal discount, BigDecimal taxPercent) {
		BigDecimal metalValue = money(weight.multiply(metalPricePerGram));
		BigDecimal subtotal = metalValue.add(orZero(makingCharges)).add(orZero(stoneCharges));
		BigDecimal disc = money(orZero(discount));
		if (disc.compareTo(subtotal) > 0) {
			throw new BadRequestException("Discount (" + disc + ") cannot be more than the price (" + subtotal + ")");
		}
		BigDecimal discountedAmount = money(subtotal.subtract(disc));
		BigDecimal taxAmount = money(discountedAmount.multiply(orZero(taxPercent)).divide(HUNDRED));
		BigDecimal finalPrice = discountedAmount.add(taxAmount);
		return new PriceBreakdown(metalValue, money(subtotal), disc, discountedAmount, taxAmount, finalPrice);
	}

	@Override
	public PriceBreakdown breakdown(Product p) {
		return calculate(p.getWeight(), p.getMetalPrice(), p.getMakingCharges(), p.getStoneCharges(),
				p.getDiscount(), p.getTax());
	}

	@Override
	public void applyPrice(Product product, BigDecimal metalPricePerGram) {
		product.setMetalPrice(metalPricePerGram);
		product.setFinalPrice(breakdown(product).finalPrice());
	}

	@Override
	public OrderTotals calculateTotals(List<PricingLine> lines, Offer offer) {
		BigDecimal subtotal = BigDecimal.ZERO;
		for (PricingLine line : lines) {
			subtotal = subtotal.add(line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())));
		}
		subtotal = money(subtotal);

		BigDecimal discount = couponDiscount(subtotal, offer);
		BigDecimal taxable = subtotal.subtract(discount);

		// Coupon discount is shared across lines in proportion to their value, then each
		// line is taxed at its own rate (products can have different tax rates).
		BigDecimal tax = BigDecimal.ZERO;
		if (subtotal.signum() > 0) {
			BigDecimal ratio = taxable.divide(subtotal, 10, RoundingMode.HALF_UP);
			for (PricingLine line : lines) {
				BigDecimal lineValue = line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())).multiply(ratio);
				tax = tax.add(lineValue.multiply(line.taxRate()).divide(HUNDRED, 10, RoundingMode.HALF_UP));
			}
		}
		tax = money(tax);

		BigDecimal delivery = money(subtotal.signum() == 0 || taxable.compareTo(freeDeliveryAbove) >= 0
				? BigDecimal.ZERO
				: deliveryCharge);
		BigDecimal total = taxable.add(tax).add(delivery);
		return new OrderTotals(subtotal, discount, tax, delivery, money(total));
	}

	@Override
	public BigDecimal couponDiscount(BigDecimal subtotal, Offer offer) {
		if (offer == null || subtotal.signum() <= 0) {
			return money(BigDecimal.ZERO);
		}
		BigDecimal discount = money(subtotal.multiply(offer.getDiscountPercent()).divide(HUNDRED));
		if (offer.getMaxDiscountAmount() != null && discount.compareTo(offer.getMaxDiscountAmount()) > 0) {
			discount = money(offer.getMaxDiscountAmount());
		}
		return discount.min(subtotal);
	}

	private static BigDecimal money(BigDecimal value) {
		return value.setScale(2, RoundingMode.HALF_UP);
	}

	private static BigDecimal orZero(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}
}
