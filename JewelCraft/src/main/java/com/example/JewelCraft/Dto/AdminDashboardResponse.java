package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
	private long totalUsers;
	private long totalCustomers;
	private long totalProducts;
	private long totalOrders;
	private BigDecimal totalRevenue;
	private long pendingOrders;
	private long lowStockProducts;
	private long deliveredOrders;
	private long cancelledOrders;
	private long openRequests;

	private List<LabelValue> ordersByStatus;
	/** label = "Jan 2026", count = orders, amount = sales. */
	private List<LabelValue> monthlySales;
	private List<LabelValue> productsByCategory;
	private List<LabelValue> usersByRole;
	private List<OrderResponse> recentOrders;
}
