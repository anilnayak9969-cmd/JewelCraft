package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.AdminUserRequest;
import com.example.JewelCraft.Dto.ApprovalRequest;
import com.example.JewelCraft.Dto.ChangePasswordRequest;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.UpdateProfileRequest;
import com.example.JewelCraft.Dto.UserResponse;
import com.example.JewelCraft.Enum.ApprovalStatus;
import com.example.JewelCraft.Enum.RoleName;

public interface UserService {

	// --- the logged-in user ---
	UserResponse getProfile(Long userId);

	UserResponse updateProfile(Long userId, UpdateProfileRequest request);

	void changePassword(Long userId, ChangePasswordRequest request);

	// --- admin user management ---
	PageResponse<UserResponse> searchUsers(RoleName role, ApprovalStatus approval, String search, int page, int size);

	/** Approve or reject a staff / store manager / delivery partner application (emails the applicant). */
	UserResponse decideApproval(Long adminId, Long id, ApprovalRequest request);

	long countPendingApprovals();

	UserResponse getUser(Long id);

	UserResponse createUser(AdminUserRequest request);

	UserResponse updateUser(Long adminId, Long id, AdminUserRequest request);

	UserResponse setEnabled(Long adminId, Long id, boolean enabled);

	void deleteUser(Long adminId, Long id);

	List<UserResponse> listDeliveryPartners();
}
