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

import com.example.JewelCraft.Dto.OfferRequest;
import com.example.JewelCraft.Dto.OfferResponse;
import com.example.JewelCraft.Services.OfferService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

	private final OfferService offerService;

	/** Public: offers shown on the home page and at checkout. */
	@GetMapping("/active")
	public List<OfferResponse> getActiveOffers() {
		return offerService.getActiveOffers();
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<OfferResponse> getAllOffers() {
		return offerService.getAllOffers();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	public OfferResponse createOffer(@Valid @RequestBody OfferRequest request) {
		return offerService.createOffer(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public OfferResponse updateOffer(@PathVariable Long id, @Valid @RequestBody OfferRequest request) {
		return offerService.updateOffer(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteOffer(@PathVariable Long id) {
		offerService.deleteOffer(id);
	}
}
