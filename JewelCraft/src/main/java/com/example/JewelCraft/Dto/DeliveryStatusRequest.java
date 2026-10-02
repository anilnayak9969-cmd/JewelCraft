package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.DeliveryStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeliveryStatusRequest {

	@NotNull(message = "Status is required")
	private DeliveryStatus status;

	@Size(max = 500)
	private String notes;
}
