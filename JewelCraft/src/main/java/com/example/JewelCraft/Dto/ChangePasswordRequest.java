package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

	@NotBlank(message = "Current password is required")
	private String currentPassword;

	@NotBlank(message = "New password is required")
	@Size(min = 8, max = 64, message = "Password must be 8-64 characters")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain letters and numbers")
	private String newPassword;
}
