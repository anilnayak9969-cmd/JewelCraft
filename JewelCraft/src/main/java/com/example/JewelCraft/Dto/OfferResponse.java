package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {
	private Long id;
	private String code;
	private String title;
	private String description;
	private BigDecimal discountPercent;
	private BigDecimal maxDiscountAmount;
	private BigDecimal minOrderAmount;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean active;
	private boolean currentlyValid;
}
