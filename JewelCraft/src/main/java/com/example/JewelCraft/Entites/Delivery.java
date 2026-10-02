package com.example.JewelCraft.Entites;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.example.JewelCraft.Enum.DeliveryStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "delivery_partner_id", nullable = false)
	private User deliveryPartner;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private DeliveryStatus status;

	@Column(length = 500)
	private String notes;

	private LocalDateTime assignedAt;
	private LocalDateTime acceptedAt;
	private LocalDateTime pickedUpAt;
	private LocalDateTime deliveredAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
