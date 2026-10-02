package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PaymentResponse;
import com.example.JewelCraft.Dto.RazorpayOrderResponse;
import com.example.JewelCraft.Dto.RazorpayVerifyRequest;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Entites.Payment;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;
import com.example.JewelCraft.Enum.TrackingEvent;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.OrderNotFoundException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.OrderMapper;
import com.example.JewelCraft.Repositories.OrderRepository;
import com.example.JewelCraft.Repositories.PaymentRepository;
import com.example.JewelCraft.Services.OrderTrackingService;
import com.example.JewelCraft.Services.PaymentService;

/**
 * Razorpay flow: (1) server creates a Razorpay order, (2) the React app opens the
 * Razorpay popup, (3) server verifies the HMAC signature Razorpay returns.
 * The payment is only marked SUCCESS in step 3, never on the client's word.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final OrderMapper mapper;
	private final OrderTrackingService tracking;
	private final String keyId;
	private final String keySecret;
	private final RestClient razorpay;

	public PaymentServiceImpl(OrderRepository orderRepository, PaymentRepository paymentRepository, OrderMapper mapper,
			OrderTrackingService tracking, @Value("${razorpay.key.id:}") String keyId, @Value("${razorpay.key.secret:}") String keySecret) {
		this.orderRepository = orderRepository;
		this.paymentRepository = paymentRepository;
		this.mapper = mapper;
		this.tracking = tracking;
		this.keyId = keyId;
		this.keySecret = keySecret;
		this.razorpay = RestClient.builder()
				.baseUrl("https://api.razorpay.com/v1")
				.defaultHeaders(h -> h.setBasicAuth(keyId, keySecret))
				.build();
	}

	@Override
	@Transactional
	public RazorpayOrderResponse createRazorpayOrder(Long userId, Long orderId) {
		if (!StringUtils.hasText(keyId) || !StringUtils.hasText(keySecret)) {
			throw new BadRequestException("Online payment is not configured. Please choose Cash on Delivery.");
		}
		Order order = findOwnedOrder(userId, orderId);
		Payment payment = order.getPayment();
		if (payment.getMethod() != PaymentMethod.RAZORPAY) {
			throw new BadRequestException("This order is set to Cash on Delivery");
		}
		if (payment.getStatus() == PaymentStatus.SUCCESS) {
			throw new BadRequestException("This order is already paid");
		}
		if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new BadRequestException("This order was cancelled");
		}

		long amountInPaise = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValueExact();
		Map<String, Object> body = Map.of("amount", amountInPaise, "currency", "INR", "receipt",
				order.getOrderNumber());
		String razorpayOrderId;
		try {
			Map<?, ?> res = razorpay.post().uri("/orders").contentType(MediaType.APPLICATION_JSON).body(body)
					.retrieve().body(Map.class);
			razorpayOrderId = res == null ? null : (String) res.get("id");
		} catch (RestClientException e) {
			log.error("Razorpay order creation failed for {}: {}", order.getOrderNumber(), e.getMessage());
			razorpayOrderId = null;
		}
		if (razorpayOrderId == null) {
			throw new BadRequestException("Could not start online payment. Please try again in a moment.");
		}
		payment.setGatewayOrderId(razorpayOrderId);
		payment.setStatus(PaymentStatus.PENDING);

		return RazorpayOrderResponse.builder()
				.keyId(keyId)
				.razorpayOrderId(razorpayOrderId)
				.amount(amountInPaise)
				.currency("INR")
				.orderId(order.getId())
				.orderNumber(order.getOrderNumber())
				.customerName(order.getUser().getFullName())
				.customerEmail(order.getUser().getEmail())
				.customerPhone(order.getUser().getPhone())
				.build();
	}

	@Override
	@Transactional
	public PaymentResponse verifyRazorpayPayment(Long userId, RazorpayVerifyRequest req) {
		Order order = findOwnedOrder(userId, req.getOrderId());
		Payment payment = order.getPayment();
		if (payment.getGatewayOrderId() == null || !payment.getGatewayOrderId().equals(req.getRazorpayOrderId())) {
			throw new BadRequestException("Payment does not belong to this order");
		}
		String expected = hmacSha256(req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId(), keySecret);
		boolean valid = MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
				req.getRazorpaySignature().getBytes(StandardCharsets.UTF_8));
		if (!valid) {
			payment.setStatus(PaymentStatus.FAILED);
			throw new BadRequestException("Payment verification failed. If money was deducted it will be refunded.");
		}
		payment.setStatus(PaymentStatus.SUCCESS);
		payment.setTransactionId(req.getRazorpayPaymentId());
		payment.setPaidAt(LocalDateTime.now());
		tracking.record(order, TrackingEvent.PAYMENT_RECEIVED, null, null);
		return mapper.toResponse(payment);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<PaymentResponse> searchPayments(PaymentStatus status, PaymentMethod method, int page, int size) {
		var pageable = ServiceUtils.page(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return PageResponse.of(paymentRepository.search(status, method, pageable), mapper::toResponse);
	}

	@Override
	@Transactional
	public PaymentResponse updateStatus(Long paymentId, PaymentStatus status) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
		if (status == PaymentStatus.REFUNDED && payment.getStatus() != PaymentStatus.SUCCESS) {
			throw new BadRequestException("Only a successful payment can be refunded");
		}
		PaymentStatus previous = payment.getStatus();
		payment.setStatus(status);
		if (status == PaymentStatus.SUCCESS && payment.getPaidAt() == null) {
			payment.setPaidAt(LocalDateTime.now());
		}
		if (status != previous && status == PaymentStatus.SUCCESS) {
			tracking.record(payment.getOrder(), TrackingEvent.PAYMENT_RECEIVED, null, null);
		} else if (status != previous && status == PaymentStatus.REFUNDED) {
			tracking.record(payment.getOrder(), TrackingEvent.PAYMENT_REFUNDED, null, null);
		}
		return mapper.toResponse(payment);
	}

	private Order findOwnedOrder(Long userId, Long orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
		if (!order.getUser().getId().equals(userId)) {
			throw new OrderNotFoundException(orderId);
		}
		return order;
	}

	private static String hmacSha256(String data, String secret) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			throw new IllegalStateException("Could not verify payment signature", e);
		}
	}
}
