package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.MetalRatePriceRequest;
import com.example.JewelCraft.Dto.MetalRateRequest;
import com.example.JewelCraft.Dto.MetalRateResponse;
import com.example.JewelCraft.Services.MetalRateService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Today's gold/silver/platinum price per gram. Changing a rate re-prices the matching products. */
@RestController
@RequestMapping("/api/metal-rates")
@RequiredArgsConstructor
public class MetalRateController {

	private final MetalRateService metalRateService;

	@GetMapping
	public List<MetalRateResponse> getRates() {
		return metalRateService.getRates();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('STORE_MANAGER', 'ADMIN')")
	public MetalRateResponse createRate(@Valid @RequestBody MetalRateRequest request) {
		return metalRateService.createRate(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('STORE_MANAGER', 'ADMIN')")
	public MetalRateResponse updateRate(@PathVariable Long id, @Valid @RequestBody MetalRatePriceRequest request) {
		return metalRateService.updateRate(id, request.pricePerGram());
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('STORE_MANAGER', 'ADMIN')")
	public void deleteRate(@PathVariable Long id) {
		metalRateService.deleteRate(id);
	}
}
