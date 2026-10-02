package com.example.JewelCraft.Entites;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.JewelCraft.Enum.OrderStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 30)
	private String orderNumber;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder.Default
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	@Embedded
	private ShippingAddress shippingAddress;

	/** Sum of item prices before tax. */
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	/** Coupon discount. */
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal discount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal tax;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal deliveryCharge;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmount;

	@Column(length = 30)
	private String couponCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	@Column(length = 500)
	private String cancelReason;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Payment payment;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Delivery delivery;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
