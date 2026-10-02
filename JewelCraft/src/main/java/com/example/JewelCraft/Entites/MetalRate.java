package com.example.JewelCraft.Entites;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.example.JewelCraft.Enum.MetalType;

import jakarta.persistence.*;
import lombok.*;

/** Current market price per gram for a metal + purity, e.g. GOLD 22K. */
@Entity
@Table(name = "metal_rates", uniqueConstraints = @UniqueConstraint(columnNames = { "metal_type", "purity" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetalRate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "metal_type", nullable = false, length = 20)
	private MetalType metalType;

	@Column(nullable = false, length = 20)
	private String purity;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal pricePerGram;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
