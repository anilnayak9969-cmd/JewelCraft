package com.example.JewelCraft.Entites;

import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.TrackingEvent;

import jakarta.persistence.*;
import lombok.*;

/** One entry in an order's tracking history: what happened, when, and who did it. */
@Entity
@Table(name = "order_tracking_events", indexes = @Index(name = "idx_tracking_order", columnList = "order_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTrackingEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TrackingEvent event;

	/** The order's status right after this event. */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus orderStatus;

	@Column(length = 500)
	private String message;

	@Column(length = 120)
	private String actorName;

	@Column(length = 30)
	private String actorRole;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}
}
