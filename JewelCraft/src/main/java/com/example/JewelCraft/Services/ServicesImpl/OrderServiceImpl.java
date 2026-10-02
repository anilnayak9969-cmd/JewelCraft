package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.JewelCraft.Dto.OrderResponse;
import com.example.JewelCraft.Dto.OrderStatusRequest;
import com.example.JewelCraft.Dto.OrderSummaryResponse;
import com.example.JewelCraft.Dto.OrderTotals;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PlaceOrderRequest;
import com.example.JewelCraft.Dto.PriceBreakdown;
import com.example.JewelCraft.Dto.PricingLine;
import com.example.JewelCraft.Dto.TrackingEventResponse;
import com.example.JewelCraft.Entites.Address;
import com.example.JewelCraft.Entites.Cart;
import com.example.JewelCraft.Entites.CartItem;
import com.example.JewelCraft.Entites.Delivery;
import com.example.JewelCraft.Entites.Inventory;
import com.example.JewelCraft.Entites.Offer;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Entites.OrderItem;
import com.example.JewelCraft.Entites.Payment;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Entites.ShippingAddress;
import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Enum.ApprovalStatus;
import com.example.JewelCraft.Enum.DeliveryStatus;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Enum.TrackingEvent;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.InsufficientStockException;
import com.example.JewelCraft.Exception.OrderNotFoundException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Exception.UnauthorizedException;
import com.example.JewelCraft.Mapper.OrderMapper;
import com.example.JewelCraft.Repositories.AddressRepository;
import com.example.JewelCraft.Repositories.CartRepository;
import com.example.JewelCraft.Repositories.OrderRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.OfferService;
import com.example.JewelCraft.Services.OrderService;
import com.example.JewelCraft.Services.OrderTrackingService;
import com.example.JewelCraft.Services.PricingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	/** Staff may only move an order through the in-store preparation steps. */
	private static final Set<OrderStatus> STAFF_ALLOWED = EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.PROCESSING,
			OrderStatus.PACKED);
	/** Only the assigned delivery partner records these, from the delivery app (see DeliveryServiceImpl). */
	private static final Set<OrderStatus> DELIVERY_STEPS = EnumSet.of(OrderStatus.SHIPPED,
			OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED);
	private static final Set<OrderStatus> ASSIGNABLE = EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.PROCESSING,
			OrderStatus.PACKED);
	private static final DateTimeFormatter ORDER_DATE = DateTimeFormatter.ofPattern("yyMMdd");

	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	private final PricingService pricingService;
	private final OfferService offerService;
	private final OrderLifecycle lifecycle;
	private final OrderMapper mapper;
	private final OrderTrackingService tracking;

	// ---------------------------------------------------------------- customer

	@Override
	@Transactional(readOnly = true)
	public OrderSummaryResponse previewOrder(Long userId, String couponCode) {
		Cart cart = cartRepository.findByUserId(userId).orElse(null);
		List<PricingLine> lines = cart == null ? List.of() : cart.getItems().stream().map(this::toLine).toList();

		Offer offer = null;
		String message = null;
		if (StringUtils.hasText(couponCode) && !lines.isEmpty()) {
			try {
				offer = offerService.validateCoupon(couponCode, pricingService.calculateTotals(lines, null).subtotal());
				message = "Coupon applied: " + offer.getTitle();
			} catch (BadRequestException e) {
				message = e.getMessage();
			}
		}
		OrderTotals totals = pricingService.calculateTotals(lines, offer);
		return OrderSummaryResponse.builder()
				.totalItems(lines.stream().mapToInt(PricingLine::quantity).sum())
				.subtotal(totals.subtotal())
				.discount(totals.discount())
				.tax(totals.tax())
				.deliveryCharge(totals.deliveryCharge())
				.total(totals.total())
				.couponCode(offer == null ? null : offer.getCode())
				.couponApplied(offer != null)
				.couponMessage(message)
				.build();
	}

	@Override
	@Transactional
	public OrderResponse placeOrder(Long userId, PlaceOrderRequest req) {
		Cart cart = cartRepository.findByUserId(userId)
				.filter(c -> !c.getItems().isEmpty())
				.orElseThrow(() -> new BadRequestException("Your cart is empty"));
		Address address = addressRepository.findByIdAndUserId(req.getAddressId(), userId)
				.orElseThrow(() -> new ResourceNotFoundException("Address", "id", req.getAddressId()));
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

		Order order = new Order();
		List<PricingLine> lines = new ArrayList<>();
		for (CartItem cartItem : cart.getItems()) {
			Product p = cartItem.getProduct();
			int qty = cartItem.getQuantity();
			if (!p.isActive()) {
				throw new BadRequestException(p.getName() + " is no longer available. Please remove it from your cart.");
			}
			Inventory inv = p.getInventory();
			if (inv == null || inv.getQuantity() < qty) {
				throw new InsufficientStockException(p.getName(), inv == null ? 0 : inv.getQuantity());
			}
			inv.setQuantity(inv.getQuantity() - qty); // reserved now, put back if the order is cancelled

			PriceBreakdown price = pricingService.breakdown(p);
			order.getItems().add(OrderItem.builder()
					.order(order)
					.product(p)
					.productName(p.getName())
					.productImage(p.getImageUrl())
					.metalDetails(p.getPurity() + " " + p.getMetalType().name().replace('_', ' ') + " · " + p.getWeight() + " g")
					.unitPrice(price.discountedAmount())
					.taxRate(p.getTax())
					.quantity(qty)
					.lineTotal(price.discountedAmount().multiply(BigDecimal.valueOf(qty)))
					.build());
			lines.add(new PricingLine(price.discountedAmount(), p.getTax(), qty));
		}

		Offer offer = null;
		if (StringUtils.hasText(req.getCouponCode())) {
			offer = offerService.validateCoupon(req.getCouponCode(), pricingService.calculateTotals(lines, null).subtotal());
		}
		OrderTotals totals = pricingService.calculateTotals(lines, offer);

		order.setOrderNumber(newOrderNumber());
		order.setUser(user);
		order.setShippingAddress(ShippingAddress.from(address));
		order.setSubtotal(totals.subtotal());
		order.setDiscount(totals.discount());
		order.setTax(totals.tax());
		order.setDeliveryCharge(totals.deliveryCharge());
		order.setTotalAmount(totals.total());
		order.setCouponCode(offer == null ? null : offer.getCode());
		order.setStatus(OrderStatus.PLACED);
		order.setPayment(Payment.builder()
				.order(order)
				.method(req.getPaymentMethod())
				.status(PaymentStatus.PENDING)
				.amount(totals.total())
				.build());

		orderRepository.save(order);
		cart.getItems().clear();
		tracking.record(order, TrackingEvent.ORDER_PLACED, null, new UserDetailsImpl(user));
		return mapper.toResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<OrderResponse> getMyOrders(Long userId, int page, int size) {
		return PageResponse.of(orderRepository.findByUserIdOrderByCreatedAtDesc(userId, ServiceUtils.page(page, size)),
				mapper::toResponse);
	}

	@Override
	@Transactional
	public OrderResponse cancelOrder(Long userId, Long orderId, String reason) {
		Order order = find(orderId);
		if (!order.getUser().getId().equals(userId)) {
			throw new OrderNotFoundException(orderId);
		}
		if (!order.getStatus().isCustomerCancellable()) {
			throw new BadRequestException("This order is already " + order.getStatus().name().toLowerCase()
					+ " and can no longer be cancelled. Please raise a request instead.");
		}
		boolean wasPaid = order.getPayment() != null && order.getPayment().getStatus() == PaymentStatus.SUCCESS;
		String why = StringUtils.hasText(reason) ? reason.trim() : "Cancelled by customer";
		lifecycle.cancel(order, why);
		tracking.recordCancellation(order, wasPaid, why, new UserDetailsImpl(order.getUser()));
		return mapper.toResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderResponse getOrder(UserDetailsImpl user, Long orderId) {
		Order order = find(orderId);
		checkCanView(user, order);
		return mapper.toResponse(order);
	}

	/** Owner, store team, or the delivery partner assigned to it. */
	private void checkCanView(UserDetailsImpl user, Order order) {
		boolean owner = order.getUser().getId().equals(user.getId());
		boolean store = user.hasRole(RoleName.STAFF, RoleName.STORE_MANAGER, RoleName.ADMIN);
		Delivery d = order.getDelivery();
		boolean assignedPartner = user.hasRole(RoleName.DELIVERY_PARTNER) && d != null
				&& d.getDeliveryPartner().getId().equals(user.getId());
		if (!owner && !store && !assignedPartner) {
			throw new UnauthorizedException("You are not allowed to view this order");
		}
	}

	// ---------------------------------------------------------------- store side

	@Override
	@Transactional(readOnly = true)
	public PageResponse<OrderResponse> searchOrders(OrderStatus status, String search, int page, int size) {
		var pageable = ServiceUtils.page(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return PageResponse.of(orderRepository.search(status, ServiceUtils.likePattern(search), pageable),
				mapper::toResponse);
	}

	@Override
	@Transactional
	public OrderResponse updateStatus(UserDetailsImpl actor, Long orderId, OrderStatusRequest req) {
		Order order = find(orderId);
		OrderStatus current = order.getStatus();
		OrderStatus target = req.getStatus();

		if (DELIVERY_STEPS.contains(target)) {
			throw new UnauthorizedException(
					"Shipped, Out for delivery and Delivered are updated only by the assigned delivery partner");
		}
		if (actor.hasRole(RoleName.STAFF) && !STAFF_ALLOWED.contains(target)) {
			throw new UnauthorizedException("Staff can only mark orders as Confirmed, Processing or Packed");
		}
		if (!current.canMoveTo(target)) {
			throw new BadRequestException("An order that is " + current + " cannot be changed to " + target);
		}
		Payment payment = order.getPayment();
		if (target == OrderStatus.CONFIRMED && payment.getMethod() == PaymentMethod.RAZORPAY
				&& payment.getStatus() != PaymentStatus.SUCCESS) {
			throw new BadRequestException("Online payment for this order is not completed yet");
		}

		if (target == OrderStatus.CANCELLED) {
			boolean wasPaid = payment.getStatus() == PaymentStatus.SUCCESS;
			String reason = StringUtils.hasText(req.getNote()) ? req.getNote().trim() : "Cancelled by the store";
			lifecycle.cancel(order, reason);
			tracking.recordCancellation(order, wasPaid, reason, actor);
		} else {
			order.setStatus(target);
			tracking.record(order, TrackingEvent.forStatus(target), req.getNote(), actor);
		}
		return mapper.toResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TrackingEventResponse> getTracking(UserDetailsImpl user, Long orderId) {
		checkCanView(user, find(orderId));
		return tracking.history(orderId);
	}

	@Override
	@Transactional
	public OrderResponse assignDelivery(UserDetailsImpl actor, Long orderId, Long partnerId) {
		Order order = find(orderId);
		User partner = userRepository.findById(partnerId)
				.filter(u -> u.isEnabled() && u.getRole().getName() == RoleName.DELIVERY_PARTNER
						&& u.getApprovalStatus() == ApprovalStatus.APPROVED)
				.orElseThrow(() -> new BadRequestException("Please choose an active delivery partner"));

		Delivery delivery = order.getDelivery();
		boolean failedEarlier = delivery != null && delivery.getStatus() == DeliveryStatus.FAILED;
		boolean pickedUp = delivery != null && (delivery.getStatus() == DeliveryStatus.PICKED_UP
				|| delivery.getStatus() == DeliveryStatus.OUT_FOR_DELIVERY
				|| delivery.getStatus() == DeliveryStatus.DELIVERED);
		boolean statusOk = ASSIGNABLE.contains(order.getStatus()) || (failedEarlier
				&& (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.OUT_FOR_DELIVERY));
		if (pickedUp || !statusOk) {
			throw new BadRequestException("A delivery partner can be assigned after the order is confirmed and before it is picked up");
		}
		// Same partner again only makes sense as a retry after a failed attempt.
		if (delivery != null && !failedEarlier && delivery.getDeliveryPartner().getId().equals(partnerId)) {
			throw new BadRequestException(partner.getFullName() + " is already assigned to this order");
		}

		if (delivery == null) {
			delivery = new Delivery();
			delivery.setOrder(order);
			order.setDelivery(delivery);
		}
		delivery.setDeliveryPartner(partner);
		delivery.setStatus(DeliveryStatus.ASSIGNED);
		delivery.setAssignedAt(LocalDateTime.now());
		delivery.setAcceptedAt(null);
		delivery.setPickedUpAt(null);
		delivery.setDeliveredAt(null);
		delivery.setNotes(null);
		orderRepository.save(order);
		tracking.record(order, TrackingEvent.DELIVERY_ASSIGNED, null, actor);
		return mapper.toResponse(order);
	}

	@Override
	@Transactional
	public OrderResponse postUpdate(UserDetailsImpl actor, Long orderId, String message) {
		Order order = find(orderId);
		if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
			throw new BadRequestException("This order is already " + order.getStatus().name().toLowerCase()
					+ ", so updates can no longer be posted");
		}
		tracking.record(order, TrackingEvent.STORE_UPDATE, message, actor);
		return mapper.toResponse(order);
	}

	// ---------------------------------------------------------------- helpers

	private Order find(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
	}

	private PricingLine toLine(CartItem item) {
		Product p = item.getProduct();
		return new PricingLine(pricingService.breakdown(p).discountedAmount(), p.getTax(), item.getQuantity());
	}

	/** e.g. JC261001-482913 */
	private String newOrderNumber() {
		String number;
		do {
			number = "JC" + LocalDate.now().format(ORDER_DATE) + "-"
					+ ThreadLocalRandom.current().nextInt(100000, 1000000);
		} while (orderRepository.existsByOrderNumber(number));
		return number;
	}
}
