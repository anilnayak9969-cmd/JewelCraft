package com.example.JewelCraft.Services;

import java.time.LocalDate;

import com.example.JewelCraft.Dto.AdminDashboardResponse;
import com.example.JewelCraft.Dto.ReportResponse;
import com.example.JewelCraft.Dto.StoreDashboardResponse;

public interface DashboardService {

	AdminDashboardResponse getAdminDashboard();

	/** includeSales = false for staff (no financial data). */
	StoreDashboardResponse getStoreDashboard(boolean includeSales);

	/** includePayments = true for admins only (money received, payment methods). */
	ReportResponse getReport(LocalDate from, LocalDate to, boolean includePayments);
}
