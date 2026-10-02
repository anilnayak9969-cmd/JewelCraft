package com.example.JewelCraft.Dto;

import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.ApprovalStatus;
import com.example.JewelCraft.Enum.RoleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Public view of a user - deliberately has no password field. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private Long id;
	private String firstName;
	private String lastName;
	private String fullName;
	private String email;
	private String phone;
	private RoleName role;
	private boolean enabled;
	private boolean emailVerified;
	private ApprovalStatus approvalStatus;
	/** Role-specific sign-up details; only filled on single-user views (profile, admin user detail). */
	private UserProfileDetails profile;
	private LocalDateTime createdAt;
}
