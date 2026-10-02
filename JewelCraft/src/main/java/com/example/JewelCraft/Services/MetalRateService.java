package com.example.JewelCraft.Services;

import java.math.BigDecimal;
import java.util.List;

import com.example.JewelCraft.Dto.MetalRateRequest;
import com.example.JewelCraft.Dto.MetalRateResponse;

public interface MetalRateService {

	List<MetalRateResponse> getRates();

	MetalRateResponse createRate(MetalRateRequest request);

	/** Changes the price per gram and re-prices every product made of that metal + purity. */
	MetalRateResponse updateRate(Long id, BigDecimal pricePerGram);

	void deleteRate(Long id);
}
