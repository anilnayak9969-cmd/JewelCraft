package com.example.JewelCraft.Services;

import java.math.BigDecimal;
import java.util.List;

import com.example.JewelCraft.Dto.OfferRequest;
import com.example.JewelCraft.Dto.OfferResponse;
import com.example.JewelCraft.Entites.Offer;

public interface OfferService {

	List<OfferResponse> getActiveOffers();

	List<OfferResponse> getAllOffers();

	OfferResponse createOffer(OfferRequest request);

	OfferResponse updateOffer(Long id, OfferRequest request);

	void deleteOffer(Long id);

	/** Returns the offer if the code can be used for this subtotal, otherwise throws BadRequestException. */
	Offer validateCoupon(String code, BigDecimal subtotal);
}
