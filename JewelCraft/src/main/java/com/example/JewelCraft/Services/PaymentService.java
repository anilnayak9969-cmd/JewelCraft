package com.example.JewelCraft.Services;

import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PaymentResponse;
import com.example.JewelCraft.Dto.RazorpayOrderResponse;
import com.example.JewelCraft.Dto.RazorpayVerifyRequest;
import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;

public interface PaymentService {

	/** Creates a Razorpay order for one of the customer's orders so the checkout popup can open. */
	RazorpayOrderResponse createRazorpayOrder(Long userId, Long orderId);

	/** Checks Razorpay's signature; marks the payment SUCCESS only if it is genuine. */
	PaymentResponse verifyRazorpayPayment(Long userId, RazorpayVerifyRequest request);

	PageResponse<PaymentResponse> searchPayments(PaymentStatus status, PaymentMethod method, int page, int size);

	PaymentResponse updateStatus(Long paymentId, PaymentStatus status);
}
