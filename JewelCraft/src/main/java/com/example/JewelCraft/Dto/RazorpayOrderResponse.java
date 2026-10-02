package com.example.JewelCraft.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Everything the React app needs to open the Razorpay checkout popup. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponse {
	private String keyId;
	private String razorpayOrderId;
	/** Amount in paise. */
	private long amount;
	private String currency;
	private Long orderId;
	private String orderNumber;
	private String customerName;
	private String customerEmail;
	private String customerPhone;
}
