package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

	@NotBlank(message = "Category name is required")
	@Size(min = 2, max = 80, message = "Category name must be 2-80 characters")
	private String name;

	@Size(max = 500)
	private String description;

	@Size(max = 255)
	private String imageUrl;

	private Boolean active;
}
