package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sales report for a date range. Payment fields (amountReceived, paymentsByMethod)
 * are only filled for admins - store managers get the store-level view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
	private LocalDate from;
	private LocalDate to;
	private long totalOrders;
	private long cancelledOrders;
	private BigDecimal totalSales;
	private BigDecimal averageOrderValue;
	private BigDecimal amountReceived;
	private List<LabelValue> ordersByStatus;
	private List<LabelValue> salesByCategory;
	private List<LabelValue> topProducts;
	private List<LabelValue> monthlySales;
	private List<LabelValue> paymentsByMethod;
}
