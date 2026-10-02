package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {

	@NotBlank(message = "Full name is required")
	@Size(max = 100)
	private String fullName;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
	private String phone;

	@NotBlank(message = "Address line 1 is required")
	@Size(max = 255)
	private String line1;

	@Size(max = 255)
	private String line2;

	@NotBlank(message = "City is required")
	@Size(max = 60)
	private String city;

	@NotBlank(message = "State is required")
	@Size(max = 60)
	private String state;

	@NotBlank(message = "Pincode is required")
	@Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
	private String pincode;

	@Size(max = 60)
	private String country;

	private boolean defaultAddress;
}
