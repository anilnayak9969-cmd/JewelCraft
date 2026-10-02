package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.OfferRequest;
import com.example.JewelCraft.Dto.OfferResponse;
import com.example.JewelCraft.Entites.Offer;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.EngagementMapper;
import com.example.JewelCraft.Repositories.OfferRepository;
import com.example.JewelCraft.Services.OfferService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

	private final OfferRepository offerRepository;
	private final EngagementMapper mapper;

	@Override
	@Transactional(readOnly = true)
	public List<OfferResponse> getActiveOffers() {
		return offerRepository.findActive(LocalDate.now()).stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OfferResponse> getAllOffers() {
		return offerRepository.findAllByOrderByEndDateDesc().stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional
	public OfferResponse createOffer(OfferRequest req) {
		String code = req.getCode().trim().toUpperCase();
		if (offerRepository.existsByCodeIgnoreCase(code)) {
			throw new BadRequestException("Coupon code " + code + " already exists");
		}
		Offer offer = new Offer();
		offer.setCode(code);
		copy(req, offer);
		offer.setActive(req.getActive() == null || req.getActive());
		return mapper.toResponse(offerRepository.save(offer));
	}

	@Override
	@Transactional
	public OfferResponse updateOffer(Long id, OfferRequest req) {
		Offer offer = find(id);
		String code = req.getCode().trim().toUpperCase();
		if (offerRepository.existsByCodeIgnoreCaseAndIdNot(code, id)) {
			throw new BadRequestException("Coupon code " + code + " already exists");
		}
		offer.setCode(code);
		copy(req, offer);
		if (req.getActive() != null) {
			offer.setActive(req.getActive());
		}
		return mapper.toResponse(offer);
	}

	@Override
	@Transactional
	public void deleteOffer(Long id) {
		offerRepository.delete(find(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Offer validateCoupon(String code, BigDecimal subtotal) {
		Offer offer = offerRepository.findByCodeIgnoreCase(code.trim())
				.orElseThrow(() -> new BadRequestException("Coupon code " + code.trim().toUpperCase() + " is not valid"));
		if (!offer.isCurrentlyValid()) {
			throw new BadRequestException("Coupon " + offer.getCode() + " has expired or is not active");
		}
		if (subtotal.compareTo(offer.getMinOrderAmount()) < 0) {
			throw new BadRequestException("Coupon " + offer.getCode() + " needs a minimum order of Rs. "
					+ offer.getMinOrderAmount().toPlainString());
		}
		return offer;
	}

	private void copy(OfferRequest req, Offer o) {
		if (req.getEndDate().isBefore(req.getStartDate())) {
			throw new BadRequestException("End date must be on or after the start date");
		}
		o.setTitle(req.getTitle().trim());
		o.setDescription(ServiceUtils.trimToNull(req.getDescription()));
		o.setDiscountPercent(req.getDiscountPercent());
		o.setMaxDiscountAmount(req.getMaxDiscountAmount());
		o.setMinOrderAmount(req.getMinOrderAmount() == null ? BigDecimal.ZERO : req.getMinOrderAmount());
		o.setStartDate(req.getStartDate());
		o.setEndDate(req.getEndDate());
	}

	private Offer find(Long id) {
		return offerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer", "id", id));
	}
}
