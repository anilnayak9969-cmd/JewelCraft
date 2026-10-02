package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RazorpayVerifyRequest {

	@NotNull
	private Long orderId;

	@NotBlank
	private String razorpayOrderId;

	@NotBlank
	private String razorpayPaymentId;

	@NotBlank
	private String razorpaySignature;
}
