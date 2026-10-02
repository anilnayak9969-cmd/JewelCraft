package com.example.JewelCraft.Dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
	private Long productId;
	private String productName;
	private String sku;
	private String categoryName;
	private String imageUrl;
	private boolean active;
	private int quantity;
	private int lowStockThreshold;
	private boolean lowStock;
	private LocalDateTime updatedAt;
}
