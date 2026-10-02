package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** A message from the store added to an order's tracking history without changing its status. */
@Data
public class OrderUpdateRequest {

	@NotBlank(message = "Please write the update for the customer")
	@Size(min = 5, max = 300, message = "The update must be 5-300 characters")
	private String message;
}
