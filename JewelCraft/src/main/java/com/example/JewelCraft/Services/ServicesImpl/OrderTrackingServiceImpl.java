package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.JewelCraft.Dto.TrackingEventResponse;
import com.example.JewelCraft.Entites.Delivery;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Entites.OrderTrackingEvent;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.TrackingEvent;
import com.example.JewelCraft.Repositories.OrderRepository;
import com.example.JewelCraft.Repositories.OrderTrackingEventRepository;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.OrderTrackingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderTrackingServiceImpl implements OrderTrackingService {

	private final OrderTrackingEventRepository eventRepository;
	private final OrderRepository orderRepository;

	@Override
	@Transactional
	public void record(Order order, TrackingEvent event, String message, UserDetailsImpl actor) {
		eventRepository.save(OrderTrackingEvent.builder()
				.order(order)
				.event(event)
				.orderStatus(order.getStatus())
				.message(StringUtils.hasText(message) ? message.trim() : defaultMessage(order, event))
				.actorName(actor == null ? "JewelCraft" : actor.getFullName())
				.actorRole(actor == null ? "SYSTEM" : actor.getRole().name())
				.build());
	}

	@Override
	@Transactional
	public void recordCancellation(Order order, boolean wasPaid, String reason, UserDetailsImpl actor) {
		record(order, TrackingEvent.CANCELLED, reason, actor);
		if (wasPaid) {
			record(order, TrackingEvent.PAYMENT_REFUNDED, null, null);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<TrackingEventResponse> history(Long orderId) {
		return eventRepository.findByOrderIdOrderByCreatedAtAscIdAsc(orderId).stream()
				.map(e -> new TrackingEventResponse(e.getId(), e.getEvent(), e.getEvent().getTitle(), e.getOrderStatus(),
						e.getMessage(), e.getActorName(), e.getActorRole(), e.getCreatedAt()))
				.toList();
	}

	@Override
	@Transactional
	public int backfillMissing() {
		Set<Long> withEvents = new HashSet<>(eventRepository.findOrderIdsWithEvents());
		int filled = 0;
		for (Order order : orderRepository.findAll()) {
			if (withEvents.contains(order.getId())) {
				continue;
			}
			eventRepository.save(OrderTrackingEvent.builder()
					.order(order)
					.event(TrackingEvent.ORDER_PLACED)
					.orderStatus(OrderStatus.PLACED)
					.message(defaultMessage(order, TrackingEvent.ORDER_PLACED))
					.actorName(order.getUser().getFullName())
					.actorRole("CUSTOMER")
					.createdAt(order.getCreatedAt())
					.build());
			if (order.getStatus() != OrderStatus.PLACED) {
				TrackingEvent current = TrackingEvent.forStatus(order.getStatus());
				eventRepository.save(OrderTrackingEvent.builder()
						.order(order)
						.event(current)
						.orderStatus(order.getStatus())
						.message(order.getStatus() == OrderStatus.CANCELLED && order.getCancelReason() != null
								? order.getCancelReason()
								: defaultMessage(order, current))
						.actorName("JewelCraft")
						.actorRole("SYSTEM")
						.createdAt(order.getUpdatedAt() != null ? order.getUpdatedAt() : order.getCreatedAt())
						.build());
			}
			filled++;
		}
		return filled;
	}

	/** Customer-friendly wording for each step. */
	private static String defaultMessage(Order order, TrackingEvent event) {
		String partner = partnerName(order);
		return switch (event) {
			case ORDER_PLACED -> "We have received your order " + order.getOrderNumber() + ".";
			case PAYMENT_RECEIVED -> "Payment of " + rupees(order.getTotalAmount()) + " received.";
			case CONFIRMED -> "Your order is confirmed and the pieces are reserved for you.";
			case PROCESSING -> "Our artisans are preparing and quality-checking your jewellery.";
			case PACKED -> "Packed in tamper-proof, fully insured packaging.";
			case DELIVERY_ASSIGNED -> partner + " will deliver your order.";
			case DELIVERY_ACCEPTED -> partner + " accepted the delivery and is heading to our store.";
			case SHIPPED -> "Picked up by " + partner + " – your order is on its way.";
			case OUT_FOR_DELIVERY -> partner + " is out for delivery and will reach you today.";
			case DELIVERED -> "Delivered. We hope you love your jewellery!";
			case DELIVERY_FAILED -> "The delivery attempt was not successful. We will arrange another attempt.";
			case CANCELLED -> "This order was cancelled.";
			case PAYMENT_REFUNDED -> "A refund of " + rupees(order.getTotalAmount())
					+ " has been initiated to your original payment method.";
			case STORE_UPDATE -> null; // always recorded with the store's own message
		};
	}

	private static String partnerName(Order order) {
		Delivery d = order.getDelivery();
		return d != null && d.getDeliveryPartner() != null ? d.getDeliveryPartner().getFullName() : "Our delivery partner";
	}

	private static String rupees(BigDecimal amount) {
		NumberFormat f = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
		f.setMaximumFractionDigits(0);
		return f.format(amount == null ? BigDecimal.ZERO : amount);
	}
}
