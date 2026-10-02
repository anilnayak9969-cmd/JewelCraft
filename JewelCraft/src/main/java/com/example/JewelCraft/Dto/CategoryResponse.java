package com.example.JewelCraft.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
	private Long id;
	private String name;
	private String description;
	private String imageUrl;
	private boolean active;
	private long productCount;
}
