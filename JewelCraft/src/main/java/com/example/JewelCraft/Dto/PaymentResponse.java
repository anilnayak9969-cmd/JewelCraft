package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
	private Long id;
	private Long orderId;
	private String orderNumber;
	private String customerName;
	private PaymentMethod method;
	private PaymentStatus status;
	private BigDecimal amount;
	private String gatewayOrderId;
	private String transactionId;
	private LocalDateTime paidAt;
	private LocalDateTime createdAt;
}
