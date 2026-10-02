package com.example.JewelCraft.Dto;

import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.TrackingEvent;

/** One row of an order's live tracking history. */
public record TrackingEventResponse(Long id, TrackingEvent event, String title, OrderStatus orderStatus, String message,
		String actorName, String actorRole, LocalDateTime createdAt) {
}
