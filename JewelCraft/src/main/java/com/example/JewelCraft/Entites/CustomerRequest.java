package com.example.JewelCraft.Entites;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.JewelCraft.Enum.RequestStatus;
import com.example.JewelCraft.Enum.RequestType;

import jakarta.persistence.*;
import lombok.*;

/** Support ticket raised by a customer (return, exchange, query...), handled by staff. */
@Entity
@Table(name = "customer_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RequestType type;

	@Column(nullable = false, length = 150)
	private String subject;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RequestStatus status;

	@Column(columnDefinition = "TEXT")
	private String response;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "handled_by")
	private User handledBy;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
