package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.MetalRateRequest;
import com.example.JewelCraft.Dto.MetalRateResponse;
import com.example.JewelCraft.Entites.MetalRate;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.ProductMapper;
import com.example.JewelCraft.Repositories.MetalRateRepository;
import com.example.JewelCraft.Repositories.ProductRepository;
import com.example.JewelCraft.Services.MetalRateService;
import com.example.JewelCraft.Services.PricingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetalRateServiceImpl implements MetalRateService {

	private final MetalRateRepository metalRateRepository;
	private final ProductRepository productRepository;
	private final PricingService pricingService;
	private final ProductMapper mapper;

	@Override
	@Transactional(readOnly = true)
	public List<MetalRateResponse> getRates() {
		return metalRateRepository.findAllByOrderByMetalTypeAscPurityDesc().stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional
	public MetalRateResponse createRate(MetalRateRequest req) {
		String purity = req.getPurity().trim().toUpperCase();
		if (metalRateRepository.findByMetalTypeAndPurityIgnoreCase(req.getMetalType(), purity).isPresent()) {
			throw new BadRequestException("A rate for " + req.getMetalType() + " " + purity + " already exists");
		}
		MetalRate rate = MetalRate.builder()
				.metalType(req.getMetalType())
				.purity(purity)
				.pricePerGram(req.getPricePerGram())
				.build();
		return mapper.toResponse(metalRateRepository.save(rate));
	}

	@Override
	@Transactional
	public MetalRateResponse updateRate(Long id, BigDecimal pricePerGram) {
		if (pricePerGram == null || pricePerGram.signum() <= 0) {
			throw new BadRequestException("Price per gram must be greater than 0");
		}
		MetalRate rate = find(id);
		rate.setPricePerGram(pricePerGram);

		List<Product> products = productRepository.findByMetalTypeAndPurityIgnoreCase(rate.getMetalType(),
				rate.getPurity());
		products.forEach(p -> pricingService.applyPrice(p, pricePerGram));
		metalRateRepository.flush(); // so updatedAt in the response is the new timestamp

		MetalRateResponse response = mapper.toResponse(rate);
		response.setProductsRepriced(products.size());
		return response;
	}

	@Override
	@Transactional
	public void deleteRate(Long id) {
		MetalRate rate = find(id);
		if (productRepository.existsByMetalTypeAndPurityIgnoreCase(rate.getMetalType(), rate.getPurity())) {
			throw new BadRequestException("Products still use " + rate.getMetalType() + " " + rate.getPurity()
					+ ". Change those products first.");
		}
		metalRateRepository.delete(rate);
	}

	private MetalRate find(Long id) {
		return metalRateRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Metal rate", "id", id));
	}
}
