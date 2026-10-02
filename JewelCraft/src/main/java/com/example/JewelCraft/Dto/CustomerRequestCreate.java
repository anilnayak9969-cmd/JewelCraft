package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.RequestType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestCreate {

	@NotNull(message = "Request type is required")
	private RequestType type;

	/** Optional: the order this request is about. */
	private Long orderId;

	@NotBlank(message = "Subject is required")
	@Size(max = 150)
	private String subject;

	@NotBlank(message = "Message is required")
	@Size(min = 10, max = 2000, message = "Message must be 10-2000 characters")
	private String message;
}
