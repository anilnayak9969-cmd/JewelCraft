package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Add an image by URL instead of uploading a file. */
@Data
public class ImageUrlRequest {

	@NotBlank(message = "Image URL is required")
	@Size(max = 500)
	@Pattern(regexp = "^https?://.+", message = "Image URL must start with http:// or https://")
	private String imageUrl;
}
