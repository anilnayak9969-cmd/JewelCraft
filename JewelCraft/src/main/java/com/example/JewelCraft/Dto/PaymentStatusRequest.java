package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentStatusRequest {

	@NotNull(message = "Status is required")
	private PaymentStatus status;
}
