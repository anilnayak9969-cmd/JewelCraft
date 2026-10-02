package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Used by both the store manager and staff dashboards; totalStoreSales is null for staff. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDashboardResponse {
	private long totalProducts;
	private long availableStock;
	private long lowStockCount;
	private long todaysOrders;
	private long pendingOrders;
	private long processingOrders;
	private long openRequests;
	private BigDecimal totalStoreSales;
	private List<InventoryResponse> lowStockItems;
	private List<OrderResponse> recentOrders;
	private List<LabelValue> ordersByStatus;
}
