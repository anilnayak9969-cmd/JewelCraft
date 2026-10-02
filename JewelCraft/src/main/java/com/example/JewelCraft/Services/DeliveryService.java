package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.DeliveryDashboardResponse;
import com.example.JewelCraft.Dto.DeliveryStatusRequest;
import com.example.JewelCraft.Dto.OrderResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;

public interface DeliveryService {

	/** Active deliveries of this partner (admins see all active deliveries). */
	List<OrderResponse> getAssignedOrders(UserDetailsImpl user);

	List<OrderResponse> getDeliveryHistory(UserDetailsImpl user);

	OrderResponse acceptDelivery(UserDetailsImpl user, Long deliveryId);

	OrderResponse updateDeliveryStatus(UserDetailsImpl user, Long deliveryId, DeliveryStatusRequest request);

	DeliveryDashboardResponse getDashboard(Long partnerId);
}
