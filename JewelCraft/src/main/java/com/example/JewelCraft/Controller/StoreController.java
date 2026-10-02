package com.example.JewelCraft.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.ReportResponse;
import com.example.JewelCraft.Dto.StoreDashboardResponse;
import com.example.JewelCraft.Dto.UserResponse;
import com.example.JewelCraft.Services.DashboardService;
import com.example.JewelCraft.Services.UserService;

import lombok.RequiredArgsConstructor;

/** STORE_MANAGER and ADMIN (enforced in WebSecurityConfig for /api/store/**). */
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

	private final DashboardService dashboardService;
	private final UserService userService;

	@GetMapping("/dashboard")
	public StoreDashboardResponse getDashboard() {
		return dashboardService.getStoreDashboard(true);
	}

	/** Store-level report: sales and products, without payment/financial breakdown. */
	@GetMapping("/reports")
	public ReportResponse getReport(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return dashboardService.getReport(from, to, false);
	}

	/** For the "assign delivery partner" drop-down. */
	@GetMapping("/delivery-partners")
	public List<UserResponse> getDeliveryPartners() {
		return userService.listDeliveryPartners();
	}
}
