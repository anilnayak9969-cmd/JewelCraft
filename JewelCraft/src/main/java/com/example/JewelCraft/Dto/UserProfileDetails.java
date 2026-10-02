package com.example.JewelCraft.Dto;

import java.time.LocalDate;

import com.example.JewelCraft.Enum.Department;
import com.example.JewelCraft.Enum.Gender;
import com.example.JewelCraft.Enum.MetalType;
import com.example.JewelCraft.Enum.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Role-specific details given at sign-up. Only the fields for the user's role are filled. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDetails {
	// customer
	private Gender gender;
	private LocalDate dateOfBirth;
	private LocalDate anniversaryDate;
	private MetalType preferredMetal;
	private Boolean marketingOptIn;
	// staff & store manager
	private String employeeCode;
	private Department department;
	private String designation;
	private String storeBranch;
	private Integer experienceYears;
	private String qualification;
	private String previousEmployer;
	// delivery partner
	private VehicleType vehicleType;
	private String vehicleNumber;
	private String drivingLicenseNumber;
	private String serviceCity;
	private String servicePincode;
	private String emergencyContactName;
	private String emergencyContactPhone;
}
