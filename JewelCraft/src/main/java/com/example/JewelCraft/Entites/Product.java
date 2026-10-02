package com.example.JewelCraft.Entites;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.JewelCraft.Enum.MetalType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String name;

	@Column(unique = true, length = 40)
	private String sku;

	@Column(columnDefinition = "TEXT")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MetalType metalType;

	@Column(nullable = false, length = 20)
	private String purity;

	/** Metal weight in grams. */
	@Column(nullable = false, precision = 10, scale = 3)
	private BigDecimal weight;

	/** Metal price per gram, copied from MetalRate whenever the price is recalculated. */
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal metalPrice;

	@Builder.Default
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal makingCharges = BigDecimal.ZERO;

	@Builder.Default
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal stoneCharges = BigDecimal.ZERO;

	/** Flat discount amount in rupees. */
	@Builder.Default
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal discount = BigDecimal.ZERO;

	/** Tax percentage, e.g. 3 for 3% GST. */
	@Builder.Default
	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal tax = new BigDecimal("3.00");

	/** Always calculated by PricingService - never accepted from the client. */
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal finalPrice;

	/** URL of the main image (kept in sync with images). */
	@Column(length = 500)
	private String imageUrl;

	@Builder.Default
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("mainImage DESC, id ASC")
	private List<ProductImage> images = new ArrayList<>();

	@OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Inventory inventory;

	private boolean featured;

	@Builder.Default
	private boolean active = true;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public int getStockQuantity() {
		return inventory == null ? 0 : inventory.getQuantity();
	}
}
