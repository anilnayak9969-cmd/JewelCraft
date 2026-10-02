package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Store manager application. Approved by an admin before the account can sign in. */
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagerRegisterRequest extends RegisterRequest {

	@NotBlank(message = "Store branch is required")
	@Size(max = 80, message = "Store branch must be at most 80 characters")
	private String storeBranch;

	@NotNull(message = "Experience is required")
	@Min(value = 1, message = "Store managers need at least 1 year of experience")
	@Max(value = 50, message = "Experience looks too large")
	private Integer experienceYears;

	@NotBlank(message = "Qualification is required")
	@Size(max = 100, message = "Qualification must be at most 100 characters")
	private String qualification;

	@Size(max = 100, message = "Previous employer must be at most 100 characters")
	private String previousEmployer;
}
