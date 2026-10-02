package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Administrator application. Only an existing admin can approve it before the account can sign in. */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminRegisterRequest extends RegisterRequest {

	@NotBlank(message = "Designation is required")
	@Size(max = 60, message = "Designation must be at most 60 characters")
	private String designation;

	@NotBlank(message = "Office location is required")
	@Size(max = 80, message = "Office location must be at most 80 characters")
	private String officeLocation;

	@NotNull(message = "Experience is required")
	@Min(value = 2, message = "Administrators need at least 2 years of experience")
	@Max(value = 50, message = "Experience looks too large")
	private Integer experienceYears;

	@NotBlank(message = "Qualification is required")
	@Size(max = 100, message = "Qualification must be at most 100 characters")
	private String qualification;

	@Size(max = 100, message = "Previous employer must be at most 100 characters")
	private String previousEmployer;
}
