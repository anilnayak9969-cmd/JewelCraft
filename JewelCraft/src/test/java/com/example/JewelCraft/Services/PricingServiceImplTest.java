package com.example.JewelCraft.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.JewelCraft.Dto.OrderTotals;
import com.example.JewelCraft.Dto.PriceBreakdown;
import com.example.JewelCraft.Dto.PricingLine;
import com.example.JewelCraft.Entites.Offer;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Services.ServicesImpl.PricingServiceImpl;

class PricingServiceImplTest {

	private final PricingServiceImpl pricing = new PricingServiceImpl(new BigDecimal("99"), new BigDecimal("5000"));

	private static BigDecimal bd(String v) {
		return new BigDecimal(v);
	}

	@Test
	void calculatesJewelleryPriceFromWeightRateChargesDiscountAndTax() {
		// 4.5 g x 7150 = 32175; + 3200 making + 0 stone = 35375; - 500 = 34875; 3% tax = 1046.25
		PriceBreakdown p = pricing.calculate(bd("4.500"), bd("7150"), bd("3200"), bd("0"), bd("500"), bd("3"));

		assertThat(p.metalValue()).isEqualByComparingTo("32175.00");
		assertThat(p.subtotal()).isEqualByComparingTo("35375.00");
		assertThat(p.discountedAmount()).isEqualByComparingTo("34875.00");
		assertThat(p.taxAmount()).isEqualByComparingTo("1046.25");
		assertThat(p.finalPrice()).isEqualByComparingTo("35921.25");
	}

	@Test
	void roundsToPaise() {
		PriceBreakdown p = pricing.calculate(bd("1.333"), bd("95"), bd("0"), bd("0"), bd("0"), bd("3"));
		assertThat(p.metalValue()).isEqualByComparingTo("126.64"); // 126.635 -> HALF_UP
		assertThat(p.finalPrice().scale()).isEqualTo(2);
	}

	@Test
	void rejectsDiscountLargerThanPrice() {
		assertThatThrownBy(() -> pricing.calculate(bd("1"), bd("100"), bd("0"), bd("0"), bd("200"), bd("3")))
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	void couponDiscountIsCappedAndTaxIsOnDiscountedAmount() {
		Offer offer = Offer.builder().code("WELCOME10").title("t").discountPercent(bd("10"))
				.maxDiscountAmount(bd("2000")).minOrderAmount(bd("0")).startDate(LocalDate.now())
				.endDate(LocalDate.now()).build();

		OrderTotals t = pricing.calculateTotals(List.of(new PricingLine(bd("34875.00"), bd("3"), 2)), offer);

		assertThat(t.subtotal()).isEqualByComparingTo("69750.00");
		assertThat(t.discount()).isEqualByComparingTo("2000.00"); // 10% would be 6975, capped at 2000
		assertThat(t.tax()).isEqualByComparingTo("2032.50"); // 3% of 67750
		assertThat(t.deliveryCharge()).isEqualByComparingTo("0.00"); // above free-delivery threshold
		assertThat(t.total()).isEqualByComparingTo("69782.50");
	}

	@Test
	void smallOrdersPayDelivery() {
		OrderTotals t = pricing.calculateTotals(List.of(new PricingLine(bd("1522.00"), bd("3"), 1)), null);

		assertThat(t.deliveryCharge()).isEqualByComparingTo("99.00");
		assertThat(t.total()).isEqualByComparingTo("1666.66"); // 1522 + 45.66 + 99
	}

	@Test
	void emptyCartCostsNothing() {
		OrderTotals t = pricing.calculateTotals(List.of(), null);
		assertThat(t.total()).isEqualByComparingTo("0");
		assertThat(t.deliveryCharge()).isEqualByComparingTo("0");
	}
}
