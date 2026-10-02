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
public class ReviewResponse {
	private Long id;
	private Long productId;
	private String productName;
	private String productImage;
	private Long userId;
	private String userName;
	private int rating;
	private String title;
	private String comment;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
