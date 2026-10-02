package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderPreviewRequest {

	@Size(max = 30)
	private String couponCode;
}
