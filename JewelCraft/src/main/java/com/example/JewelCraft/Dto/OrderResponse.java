package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.JewelCraft.Enum.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
	private Long id;
	private String orderNumber;
	private OrderStatus status;
	private Long customerId;
	private String customerName;
	private String customerEmail;
	private String customerPhone;
	private List<OrderItemResponse> items;
	private int totalItems;
	private AddressResponse shippingAddress;
	private BigDecimal subtotal;
	private BigDecimal discount;
	private BigDecimal tax;
	private BigDecimal deliveryCharge;
	private BigDecimal totalAmount;
	private String couponCode;
	private String cancelReason;
	private boolean cancellable;
	private PaymentResponse payment;
	private DeliveryResponse delivery;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
