package com.example.JewelCraft.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PaymentResponse;
import com.example.JewelCraft.Dto.PaymentStatusRequest;
import com.example.JewelCraft.Dto.RazorpayOrderResponse;
import com.example.JewelCraft.Dto.RazorpayVerifyRequest;
import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/razorpay/order/{orderId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public RazorpayOrderResponse createRazorpayOrder(@AuthenticationPrincipal UserDetailsImpl user,
			@PathVariable Long orderId) {
		return paymentService.createRazorpayOrder(user.getId(), orderId);
	}

	@PostMapping("/razorpay/verify")
	@PreAuthorize("hasRole('CUSTOMER')")
	public PaymentResponse verifyRazorpayPayment(@AuthenticationPrincipal UserDetailsImpl user,
			@Valid @RequestBody RazorpayVerifyRequest request) {
		return paymentService.verifyRazorpayPayment(user.getId(), request);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public PageResponse<PaymentResponse> searchPayments(@RequestParam(required = false) PaymentStatus status,
			@RequestParam(required = false) PaymentMethod method, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return paymentService.searchPayments(status, method, page, size);
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public PaymentResponse updateStatus(@PathVariable Long id, @Valid @RequestBody PaymentStatusRequest request) {
		return paymentService.updateStatus(id, request.getStatus());
	}
}
