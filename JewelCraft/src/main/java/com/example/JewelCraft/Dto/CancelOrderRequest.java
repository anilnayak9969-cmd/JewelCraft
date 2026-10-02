package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelOrderRequest {

	@Size(max = 500, message = "Reason must be at most 500 characters")
	private String reason;
}
