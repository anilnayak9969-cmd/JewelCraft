package com.example.JewelCraft.Controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.ActiveRequest;
import com.example.JewelCraft.Dto.AdminDashboardResponse;
import com.example.JewelCraft.Dto.AdminUserRequest;
import com.example.JewelCraft.Dto.ApprovalRequest;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.ReportResponse;
import com.example.JewelCraft.Dto.UserResponse;
import com.example.JewelCraft.Enum.ApprovalStatus;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.DashboardService;
import com.example.JewelCraft.Services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** ADMIN only (enforced in WebSecurityConfig for /api/admin/**). */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final DashboardService dashboardService;
	private final UserService userService;

	@GetMapping("/dashboard")
	public AdminDashboardResponse getDashboard() {
		return dashboardService.getAdminDashboard();
	}

	@GetMapping("/reports")
	public ReportResponse getReport(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return dashboardService.getReport(from, to, true);
	}

	// ------------------------------------------------ users (customers, staff, managers, delivery, admins)

	@GetMapping("/users")
	public PageResponse<UserResponse> searchUsers(@RequestParam(required = false) RoleName role,
			@RequestParam(required = false) ApprovalStatus approval, @RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "15") int size) {
		return userService.searchUsers(role, approval, search, page, size);
	}

	/** Badge count for the "Pending approval" tab. */
	@GetMapping("/users/pending-count")
	public Map<String, Long> pendingCount() {
		return Map.of("count", userService.countPendingApprovals());
	}

	/** Approve or reject a staff / store manager / delivery partner application. */
	@PutMapping("/users/{id}/approval")
	public UserResponse decideApproval(@AuthenticationPrincipal UserDetailsImpl admin, @PathVariable Long id,
			@Valid @RequestBody ApprovalRequest request) {
		return userService.decideApproval(admin.getId(), id, request);
	}

	@GetMapping("/users/{id}")
	public UserResponse getUser(@PathVariable Long id) {
		return userService.getUser(id);
	}

	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse createUser(@Valid @RequestBody AdminUserRequest request) {
		return userService.createUser(request);
	}

	@PutMapping("/users/{id}")
	public UserResponse updateUser(@AuthenticationPrincipal UserDetailsImpl admin, @PathVariable Long id,
			@Valid @RequestBody AdminUserRequest request) {
		return userService.updateUser(admin.getId(), id, request);
	}

	@PatchMapping("/users/{id}/status")
	public UserResponse setEnabled(@AuthenticationPrincipal UserDetailsImpl admin, @PathVariable Long id,
			@Valid @RequestBody ActiveRequest request) {
		return userService.setEnabled(admin.getId(), id, request.active());
	}

	@DeleteMapping("/users/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@AuthenticationPrincipal UserDetailsImpl admin, @PathVariable Long id) {
		userService.deleteUser(admin.getId(), id);
	}
}
