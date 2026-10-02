package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignDeliveryRequest {

	@NotNull(message = "Delivery partner is required")
	private Long deliveryPartnerId;
}
