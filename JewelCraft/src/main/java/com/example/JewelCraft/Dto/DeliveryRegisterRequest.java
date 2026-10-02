package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.VehicleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Delivery partner application. Vehicle number and driving licence are required for
 * motor vehicles (checked in the service, since a bicycle needs neither).
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryRegisterRequest extends RegisterRequest {

	@NotNull(message = "Please choose your vehicle")
	private VehicleType vehicleType;

	/** Indian plate, e.g. MH 12 AB 1234 (spaces/hyphens optional). */
	@Pattern(regexp = "^$|^[A-Za-z]{2}[ -]?\\d{1,2}[ -]?[A-Za-z]{0,3}[ -]?\\d{4}$",
			message = "Enter a valid vehicle number, e.g. MH 12 AB 1234")
	private String vehicleNumber;

	/** Indian DL, e.g. MH12 20110012345 (state, RTO, year, 7 digits). */
	@Pattern(regexp = "^$|^[A-Za-z]{2}[ -]?\\d{2}[ -]?\\d{4}[ -]?\\d{7}$",
			message = "Enter a valid licence number, e.g. MH12 20110012345")
	private String drivingLicenseNumber;

	@NotBlank(message = "Service city is required")
	@Size(max = 60, message = "City must be at most 60 characters")
	private String serviceCity;

	@NotBlank(message = "Service pincode is required")
	@Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
	private String servicePincode;

	@Size(max = 100, message = "Name must be at most 100 characters")
	private String emergencyContactName;

	@Pattern(regexp = "^$|^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
	private String emergencyContactPhone;
}
