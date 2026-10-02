package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

import com.example.JewelCraft.Enum.MetalType;

import lombok.Data;

/** Query parameters for the product listing (bound from ?search=&categoryId=...). */
@Data
public class ProductFilter {
	private String search;
	private Long categoryId;
	private MetalType metalType;
	private String purity;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private Boolean featured;
	private Boolean inStock;
	/** Only used by management screens. */
	private Boolean active;
	/** newest | price_asc | price_desc | name_asc | weight_desc */
	private String sort;
	private int page = 0;
	private int size = 12;
}
