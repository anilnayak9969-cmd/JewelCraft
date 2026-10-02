package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.RoleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Returned after sign-up: the account exists but the email OTP still has to be entered. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
	private String email;
	private RoleName role;
	private String message;
	private long otpExpiresInSeconds;
	private long resendAvailableInSeconds;
	/** True for staff, store manager and delivery partner sign-ups. */
	private boolean requiresApproval;
}
