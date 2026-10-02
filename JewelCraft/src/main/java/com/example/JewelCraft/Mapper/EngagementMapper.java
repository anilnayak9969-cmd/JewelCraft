package com.example.JewelCraft.Mapper;

import org.springframework.stereotype.Component;

import com.example.JewelCraft.Dto.CustomerRequestResponse;
import com.example.JewelCraft.Dto.OfferResponse;
import com.example.JewelCraft.Dto.ReviewResponse;
import com.example.JewelCraft.Entites.CustomerRequest;
import com.example.JewelCraft.Entites.Offer;
import com.example.JewelCraft.Entites.Review;

/** Reviews, offers and customer requests. */
@Component
public class EngagementMapper {

	public ReviewResponse toResponse(Review r) {
		return ReviewResponse.builder()
				.id(r.getId())
				.productId(r.getProduct().getId())
				.productName(r.getProduct().getName())
				.productImage(r.getProduct().getImageUrl())
				.userId(r.getUser().getId())
				.userName(r.getUser().getFullName())
				.rating(r.getRating())
				.title(r.getTitle())
				.comment(r.getComment())
				.createdAt(r.getCreatedAt())
				.updatedAt(r.getUpdatedAt())
				.build();
	}

	public OfferResponse toResponse(Offer o) {
		return OfferResponse.builder()
				.id(o.getId())
				.code(o.getCode())
				.title(o.getTitle())
				.description(o.getDescription())
				.discountPercent(o.getDiscountPercent())
				.maxDiscountAmount(o.getMaxDiscountAmount())
				.minOrderAmount(o.getMinOrderAmount())
				.startDate(o.getStartDate())
				.endDate(o.getEndDate())
				.active(o.isActive())
				.currentlyValid(o.isCurrentlyValid())
				.build();
	}

	public CustomerRequestResponse toResponse(CustomerRequest r) {
		return CustomerRequestResponse.builder()
				.id(r.getId())
				.customerName(r.getUser().getFullName())
				.customerEmail(r.getUser().getEmail())
				.orderId(r.getOrder() == null ? null : r.getOrder().getId())
				.orderNumber(r.getOrder() == null ? null : r.getOrder().getOrderNumber())
				.type(r.getType())
				.subject(r.getSubject())
				.message(r.getMessage())
				.status(r.getStatus())
				.response(r.getResponse())
				.handledBy(r.getHandledBy() == null ? null : r.getHandledBy().getFullName())
				.createdAt(r.getCreatedAt())
				.updatedAt(r.getUpdatedAt())
				.build();
	}
}
