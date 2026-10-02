package com.example.JewelCraft.Dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
	private Long id;
	private ProductResponse product;
	private LocalDateTime addedAt;
}
