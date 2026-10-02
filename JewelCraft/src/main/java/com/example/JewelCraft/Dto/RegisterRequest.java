package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name must be at most 50 characters")
	private String firstName;

	@Size(max = 50, message = "Last name must be at most 50 characters")
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 64, message = "Password must be 8-64 characters")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain letters and numbers")
	private String password;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
	private String phone;
}
