package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Just an email address, e.g. to resend a verification code. */
@Data
public class EmailRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	private String email;
}
