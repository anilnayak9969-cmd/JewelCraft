package com.example.JewelCraft.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.StoreDashboardResponse;
import com.example.JewelCraft.Services.DashboardService;

import lombok.RequiredArgsConstructor;

/** STAFF, STORE_MANAGER and ADMIN (enforced in WebSecurityConfig for /api/staff/**). */
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

	private final DashboardService dashboardService;

	/** Same operational numbers as the store dashboard, but without sales figures. */
	@GetMapping("/dashboard")
	public StoreDashboardResponse getDashboard() {
		return dashboardService.getStoreDashboard(false);
	}
}
