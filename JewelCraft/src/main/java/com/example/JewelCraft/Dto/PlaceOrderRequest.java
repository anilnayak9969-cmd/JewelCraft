package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Items come from the server-side cart; prices are always recalculated on the server. */
@Data
public class PlaceOrderRequest {

	@NotNull(message = "Please select a delivery address")
	private Long addressId;

	@NotNull(message = "Please select a payment method")
	private PaymentMethod paymentMethod;

	@Size(max = 30)
	private String couponCode;
}
