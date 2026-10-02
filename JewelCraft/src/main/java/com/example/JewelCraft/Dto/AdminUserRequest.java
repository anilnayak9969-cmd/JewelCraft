package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.RoleName;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Used by admins to create or edit any user (staff, managers, delivery partners...). */
@Data
public class AdminUserRequest {

	@NotBlank(message = "First name is required")
	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	private String email;

	/** Required when creating; leave blank on update to keep the current password. */
	@Size(min = 8, max = 64, message = "Password must be 8-64 characters")
	private String password;

	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
	private String phone;

	@NotNull(message = "Role is required")
	private RoleName role;

	private Boolean enabled;
}
