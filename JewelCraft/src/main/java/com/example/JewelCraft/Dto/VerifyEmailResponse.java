package com.example.JewelCraft.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Customers are signed in straight after verifying (auth is filled).
 * Staff-type accounts get requiresApproval=true and no token until an admin approves them.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailResponse {
	private boolean verified;
	private boolean requiresApproval;
	private String message;
	private AuthResponse auth;
}
