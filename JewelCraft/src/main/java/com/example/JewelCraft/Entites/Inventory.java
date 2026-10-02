package com.example.JewelCraft.Entites;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false, unique = true)
	private Product product;

	@Builder.Default
	@Column(nullable = false)
	private int quantity = 0;

	@Builder.Default
	@Column(nullable = false)
	private int lowStockThreshold = 5;

	/** Optimistic lock so two simultaneous checkouts cannot oversell the same piece. */
	@Version
	private Long version;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public boolean isLowStock() {
		return quantity <= lowStockThreshold;
	}
}
