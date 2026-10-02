package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewRequest {

	@Min(value = 1, message = "Rating must be between 1 and 5")
	@Max(value = 5, message = "Rating must be between 1 and 5")
	private int rating;

	@Size(max = 100, message = "Title must be at most 100 characters")
	private String title;

	@NotBlank(message = "Please write a short review")
	@Size(min = 5, max = 1000, message = "Review must be 5-1000 characters")
	private String comment;
}
