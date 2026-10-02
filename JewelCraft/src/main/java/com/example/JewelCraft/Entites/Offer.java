package com.example.JewelCraft.Entites;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

/** Coupon code applied at checkout. */
@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 30)
	private String code;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(length = 500)
	private String description;

	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal discountPercent;

	/** Upper limit on the discount; null means no limit. */
	@Column(precision = 12, scale = 2)
	private BigDecimal maxDiscountAmount;

	@Builder.Default
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal minOrderAmount = BigDecimal.ZERO;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Builder.Default
	private boolean active = true;

	public boolean isCurrentlyValid() {
		LocalDate today = LocalDate.now();
		return active && !today.isBefore(startDate) && !today.isAfter(endDate);
	}
}
