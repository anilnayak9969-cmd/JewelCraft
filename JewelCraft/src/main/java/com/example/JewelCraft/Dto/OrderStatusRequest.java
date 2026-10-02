package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.OrderStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderStatusRequest {

	@NotNull(message = "Status is required")
	private OrderStatus status;

	/** Shown to the customer in tracking instead of the standard message; the reason when cancelling. */
	@Size(max = 500)
	private String note;
}
