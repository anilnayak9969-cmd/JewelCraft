package com.example.JewelCraft.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.MetalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetalRateResponse {
	private Long id;
	private MetalType metalType;
	private String purity;
	private BigDecimal pricePerGram;
	private LocalDateTime updatedAt;
	/** How many products were re-priced by the last change (only set after an update). */
	private Integer productsRepriced;
}
