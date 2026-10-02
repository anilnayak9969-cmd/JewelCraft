package com.example.JewelCraft.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDashboardResponse {
	private long newAssignments;
	private long inProgress;
	private long deliveredToday;
	private long totalDelivered;
	private long failed;
}
