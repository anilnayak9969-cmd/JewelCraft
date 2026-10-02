package com.example.JewelCraft.Services.ServicesImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.JewelCraft.Dto.DeliveryDashboardResponse;
import com.example.JewelCraft.Dto.DeliveryStatusRequest;
import com.example.JewelCraft.Dto.OrderResponse;
import com.example.JewelCraft.Entites.Delivery;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Enum.DeliveryStatus;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Enum.TrackingEvent;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Exception.UnauthorizedException;
import com.example.JewelCraft.Mapper.OrderMapper;
import com.example.JewelCraft.Repositories.DeliveryRepository;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.DeliveryService;
import com.example.JewelCraft.Services.OrderTrackingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

	private static final Set<DeliveryStatus> ACTIVE = EnumSet.of(DeliveryStatus.ASSIGNED, DeliveryStatus.ACCEPTED,
			DeliveryStatus.PICKED_UP, DeliveryStatus.OUT_FOR_DELIVERY);
	private static final Set<DeliveryStatus> IN_PROGRESS = EnumSet.of(DeliveryStatus.ACCEPTED,
			DeliveryStatus.PICKED_UP, DeliveryStatus.OUT_FOR_DELIVERY);
	private static final Set<DeliveryStatus> FINISHED = EnumSet.of(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED);
	private static final Set<OrderStatus> READY_FOR_PICKUP = EnumSet.of(OrderStatus.PACKED, OrderStatus.SHIPPED,
			OrderStatus.OUT_FOR_DELIVERY);

	private final DeliveryRepository deliveryRepository;
	private final OrderLifecycle lifecycle;
	private final OrderMapper orderMapper;
	private final OrderTrackingService tracking;

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getAssignedOrders(UserDetailsImpl user) {
		return list(user, ACTIVE);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getDeliveryHistory(UserDetailsImpl user) {
		return list(user, FINISHED);
	}

	private List<OrderResponse> list(UserDetailsImpl user, Set<DeliveryStatus> statuses) {
		List<Delivery> deliveries = user.hasRole(RoleName.ADMIN)
				? deliveryRepository.findByStatusInOrderByAssignedAtDesc(statuses)
				: deliveryRepository.findByDeliveryPartnerIdAndStatusInOrderByAssignedAtDesc(user.getId(), statuses);
		return deliveries.stream().map(d -> orderMapper.toResponse(d.getOrder())).toList();
	}

	@Override
	@Transactional
	public OrderResponse acceptDelivery(UserDetailsImpl user, Long deliveryId) {
		Delivery d = findOwned(user, deliveryId);
		if (d.getStatus() != DeliveryStatus.ASSIGNED) {
			throw new BadRequestException("This delivery is already " + d.getStatus());
		}
		d.setStatus(DeliveryStatus.ACCEPTED);
		d.setAcceptedAt(LocalDateTime.now());
		tracking.record(d.getOrder(), TrackingEvent.DELIVERY_ACCEPTED, null, user);
		return orderMapper.toResponse(d.getOrder());
	}

	@Override
	@Transactional
	public OrderResponse updateDeliveryStatus(UserDetailsImpl user, Long deliveryId, DeliveryStatusRequest req) {
		Delivery d = findOwned(user, deliveryId);
		DeliveryStatus target = req.getStatus();
		if (!d.getStatus().canMoveTo(target)) {
			throw new BadRequestException("A delivery that is " + d.getStatus() + " cannot be changed to " + target);
		}
		Order order = d.getOrder();
		if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new BadRequestException("This order was cancelled");
		}

		switch (target) {
			case PICKED_UP -> {
				if (!READY_FOR_PICKUP.contains(order.getStatus())) {
					throw new BadRequestException("The store has not packed this order yet");
				}
				d.setPickedUpAt(LocalDateTime.now());
				if (order.getStatus() == OrderStatus.PACKED) {
					order.setStatus(OrderStatus.SHIPPED);
				}
			}
			case OUT_FOR_DELIVERY -> order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
			case DELIVERED -> lifecycle.markDelivered(order);
			case FAILED -> {
				if (!StringUtils.hasText(req.getNotes())) {
					throw new BadRequestException("Please add a note explaining why the delivery failed");
				}
			}
			default -> {
			}
		}
		d.setStatus(target);
		if (StringUtils.hasText(req.getNotes())) {
			d.setNotes(req.getNotes().trim());
		}
		TrackingEvent event = switch (target) {
			case PICKED_UP -> TrackingEvent.SHIPPED;
			case OUT_FOR_DELIVERY -> TrackingEvent.OUT_FOR_DELIVERY;
			case DELIVERED -> TrackingEvent.DELIVERED;
			case FAILED -> TrackingEvent.DELIVERY_FAILED;
			default -> null;
		};
		if (event != null) {
			// For a failed attempt the partner's note is shown to the customer.
			tracking.record(order, event, target == DeliveryStatus.FAILED ? req.getNotes() : null, user);
		}
		return orderMapper.toResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public DeliveryDashboardResponse getDashboard(Long partnerId) {
		return DeliveryDashboardResponse.builder()
				.newAssignments(deliveryRepository.countByDeliveryPartnerIdAndStatusIn(partnerId,
						Set.of(DeliveryStatus.ASSIGNED)))
				.inProgress(deliveryRepository.countByDeliveryPartnerIdAndStatusIn(partnerId, IN_PROGRESS))
				.deliveredToday(deliveryRepository.countByDeliveryPartnerIdAndStatusAndDeliveredAtGreaterThanEqual(
						partnerId, DeliveryStatus.DELIVERED, LocalDate.now().atStartOfDay()))
				.totalDelivered(deliveryRepository.countByDeliveryPartnerIdAndStatusIn(partnerId,
						Set.of(DeliveryStatus.DELIVERED)))
				.failed(deliveryRepository.countByDeliveryPartnerIdAndStatusIn(partnerId, Set.of(DeliveryStatus.FAILED)))
				.build();
	}

	/** A partner can only act on deliveries assigned to them; admins can act on any. */
	private Delivery findOwned(UserDetailsImpl user, Long deliveryId) {
		Delivery d = deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new ResourceNotFoundException("Delivery", "id", deliveryId));
		// Only the assigned partner may accept or update a delivery (admins can view, not act).
		if (!d.getDeliveryPartner().getId().equals(user.getId())) {
			throw new UnauthorizedException("This delivery is not assigned to you");
		}
		return d;
	}
}
