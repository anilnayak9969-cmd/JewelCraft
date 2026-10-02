package com.example.JewelCraft.Entites;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentMethod method;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentStatus status;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	/** Razorpay order id (order_xxx). */
	@Column(length = 60)
	private String gatewayOrderId;

	/** Razorpay payment id (pay_xxx). */
	@Column(length = 60)
	private String transactionId;

	private LocalDateTime paidAt;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
}
