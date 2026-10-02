package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyEmailRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	private String email;

	@NotBlank(message = "Please enter the 6-digit code")
	@Pattern(regexp = "^\\d{6}$", message = "The code is 6 digits")
	private String otp;
}
