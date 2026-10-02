package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

import com.example.JewelCraft.Enum.MetalType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MetalRateRequest {

	@NotNull(message = "Metal type is required")
	private MetalType metalType;

	@NotBlank(message = "Purity is required")
	@Size(max = 20)
	private String purity;

	@NotNull(message = "Price per gram is required")
	@DecimalMin(value = "0.01", message = "Price per gram must be greater than 0")
	private BigDecimal pricePerGram;
}
