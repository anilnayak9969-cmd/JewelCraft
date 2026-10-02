package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

	@NotBlank(message = "First name is required")
	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
	private String phone;
}
