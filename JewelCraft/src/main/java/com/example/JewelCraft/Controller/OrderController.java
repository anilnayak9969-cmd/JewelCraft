package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.AssignDeliveryRequest;
import com.example.JewelCraft.Dto.CancelOrderRequest;
import com.example.JewelCraft.Dto.OrderPreviewRequest;
import com.example.JewelCraft.Dto.OrderResponse;
import com.example.JewelCraft.Dto.OrderStatusRequest;
import com.example.JewelCraft.Dto.OrderSummaryResponse;
import com.example.JewelCraft.Dto.OrderUpdateRequest;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PlaceOrderRequest;
import com.example.JewelCraft.Dto.TrackingEventResponse;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	// ------------------------------------------------ customer

	/** Checkout totals for the current cart (+ optional coupon) before placing the order. */
	@PostMapping("/preview")
	@PreAuthorize("hasRole('CUSTOMER')")
	public OrderSummaryResponse preview(@AuthenticationPrincipal UserDetailsImpl user,
			@Valid @RequestBody OrderPreviewRequest request) {
		return orderService.previewOrder(user.getId(), request.getCouponCode());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('CUSTOMER')")
	public OrderResponse placeOrder(@AuthenticationPrincipal UserDetailsImpl user,
			@Valid @RequestBody PlaceOrderRequest request) {
		return orderService.placeOrder(user.getId(), request);
	}

	@GetMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public PageResponse<OrderResponse> getMyOrders(@AuthenticationPrincipal UserDetailsImpl user,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		return orderService.getMyOrders(user.getId(), page, size);
	}

	@PostMapping("/{id}/cancel")
	@PreAuthorize("hasRole('CUSTOMER')")
	public OrderResponse cancelOrder(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
			@Valid @RequestBody(required = false) CancelOrderRequest request) {
		return orderService.cancelOrder(user.getId(), id, request == null ? null : request.getReason());
	}

	/** Live tracking history (polled by the order page every few seconds). */
	@GetMapping("/{id}/tracking")
	public List<TrackingEventResponse> getTracking(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
		return orderService.getTracking(user, id);
	}

	/** Owner, store team, or the assigned delivery partner (checked in the service). */
	@GetMapping("/{id}")
	public OrderResponse getOrder(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
		return orderService.getOrder(user, id);
	}

	// ------------------------------------------------ store team

	@GetMapping("/manage")
	@PreAuthorize("hasAnyRole('STAFF', 'STORE_MANAGER', 'ADMIN')")
	public PageResponse<OrderResponse> searchOrders(@RequestParam(required = false) OrderStatus status,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "15") int size) {
		return orderService.searchOrders(status, search, page, size);
	}

	/** Staff are limited to CONFIRMED / PROCESSING / PACKED (checked in the service). */
	@PutMapping("/{id}/status")
	@PreAuthorize("hasAnyRole('STAFF', 'STORE_MANAGER', 'ADMIN')")
	public OrderResponse updateStatus(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
			@Valid @RequestBody OrderStatusRequest request) {
		return orderService.updateStatus(user, id, request);
	}

	@PutMapping("/{id}/assign-delivery")
	@PreAuthorize("hasAnyRole('STORE_MANAGER', 'ADMIN')")
	public OrderResponse assignDelivery(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
			@Valid @RequestBody AssignDeliveryRequest request) {
		return orderService.assignDelivery(user, id, request.getDeliveryPartnerId());
	}

	/** A message for the customer (e.g. a delay) that doesn't change the order's status. */
	@PostMapping("/{id}/updates")
	@PreAuthorize("hasAnyRole('STORE_MANAGER', 'ADMIN')")
	public OrderResponse postUpdate(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
			@Valid @RequestBody OrderUpdateRequest request) {
		return orderService.postUpdate(user, id, request.getMessage());
	}
}
