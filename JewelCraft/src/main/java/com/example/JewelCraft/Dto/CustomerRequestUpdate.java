package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.RequestStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestUpdate {

	@NotNull(message = "Status is required")
	private RequestStatus status;

	@Size(max = 2000)
	private String response;
}
