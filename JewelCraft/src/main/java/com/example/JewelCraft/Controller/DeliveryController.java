package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.DeliveryDashboardResponse;
import com.example.JewelCraft.Dto.DeliveryStatusRequest;
import com.example.JewelCraft.Dto.OrderResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.DeliveryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** DELIVERY_PARTNER and ADMIN (enforced in WebSecurityConfig). Path ids are delivery ids. */
@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

	private final DeliveryService deliveryService;

	@GetMapping("/dashboard")
	public DeliveryDashboardResponse getDashboard(@AuthenticationPrincipal UserDetailsImpl user) {
		return deliveryService.getDashboard(user.getId());
	}

	@GetMapping("/orders")
	public List<OrderResponse> getAssignedOrders(@AuthenticationPrincipal UserDetailsImpl user) {
		return deliveryService.getAssignedOrders(user);
	}

	@GetMapping("/history")
	public List<OrderResponse> getHistory(@AuthenticationPrincipal UserDetailsImpl user) {
		return deliveryService.getDeliveryHistory(user);
	}

	@PutMapping("/{deliveryId}/accept")
	public OrderResponse accept(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long deliveryId) {
		return deliveryService.acceptDelivery(user, deliveryId);
	}

	@PutMapping("/{deliveryId}/status")
	public OrderResponse updateStatus(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long deliveryId,
			@Valid @RequestBody DeliveryStatusRequest request) {
		return deliveryService.updateDeliveryStatus(user, deliveryId, request);
	}
}
