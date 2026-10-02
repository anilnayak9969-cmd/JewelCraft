package com.example.JewelCraft.Entites;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

/** Product details are copied so the order stays correct if the product is later edited. */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(nullable = false, length = 150)
	private String productName;

	@Column(length = 500)
	private String productImage;

	@Column(length = 60)
	private String metalDetails;

	/** Unit price before tax (after the product discount). */
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal unitPrice;

	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal taxRate;

	@Column(nullable = false)
	private int quantity;

	/** unitPrice x quantity. */
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal lineTotal;
}
