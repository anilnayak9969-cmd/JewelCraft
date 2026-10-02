package com.example.JewelCraft.Dto;

import java.time.LocalDate;

import com.example.JewelCraft.Enum.Gender;
import com.example.JewelCraft.Enum.MetalType;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Customer sign-up: the common fields plus optional occasion details for birthday/anniversary offers. */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerRegisterRequest extends RegisterRequest {

	private Gender gender;

	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;

	@PastOrPresent(message = "Anniversary cannot be in the future")
	private LocalDate anniversaryDate;

	private MetalType preferredMetal;

	private Boolean marketingOptIn;
}
