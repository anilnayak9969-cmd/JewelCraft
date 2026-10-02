package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.TrackingEventResponse;

import com.example.JewelCraft.Dto.OrderResponse;
import com.example.JewelCraft.Dto.OrderStatusRequest;
import com.example.JewelCraft.Dto.OrderSummaryResponse;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PlaceOrderRequest;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Security.UserDetailsImpl;

public interface OrderService {

	// --- customer ---
	OrderSummaryResponse previewOrder(Long userId, String couponCode);

	OrderResponse placeOrder(Long userId, PlaceOrderRequest request);

	PageResponse<OrderResponse> getMyOrders(Long userId, int page, int size);

	OrderResponse cancelOrder(Long userId, Long orderId, String reason);

	/** Owner, store staff, or the assigned delivery partner. */
	OrderResponse getOrder(UserDetailsImpl user, Long orderId);

	/** Step-by-step live tracking history; same access rules as getOrder. */
	List<TrackingEventResponse> getTracking(UserDetailsImpl user, Long orderId);

	// --- staff / store manager / admin ---
	PageResponse<OrderResponse> searchOrders(OrderStatus status, String search, int page, int size);

	OrderResponse updateStatus(UserDetailsImpl actor, Long orderId, OrderStatusRequest request);

	OrderResponse assignDelivery(UserDetailsImpl actor, Long orderId, Long deliveryPartnerId);

	/** Adds a store message to the tracking history without changing the order's status. */
	OrderResponse postUpdate(UserDetailsImpl actor, Long orderId, String message);
}
