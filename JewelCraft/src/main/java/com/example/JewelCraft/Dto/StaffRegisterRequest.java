package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.Department;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Store staff application. Approved by an admin before the account can sign in. */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffRegisterRequest extends RegisterRequest {

	@NotNull(message = "Please choose a department")
	private Department department;

	@NotBlank(message = "Designation is required")
	@Size(max = 60, message = "Designation must be at most 60 characters")
	private String designation;

	@NotBlank(message = "Store branch is required")
	@Size(max = 80, message = "Store branch must be at most 80 characters")
	private String storeBranch;

	@NotNull(message = "Experience is required")
	@Min(value = 0, message = "Experience cannot be negative")
	@Max(value = 50, message = "Experience looks too large")
	private Integer experienceYears;

	@Size(max = 100, message = "Qualification must be at most 100 characters")
	private String qualification;
}
