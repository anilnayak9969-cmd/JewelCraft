package com.example.JewelCraft.Enum;

import java.util.Map;
import java.util.Set;

public enum DeliveryStatus {
	ASSIGNED, ACCEPTED, PICKED_UP, OUT_FOR_DELIVERY, DELIVERED, FAILED;

	private static final Map<DeliveryStatus, Set<DeliveryStatus>> TRANSITIONS = Map.of(
			ASSIGNED, Set.of(ACCEPTED),
			ACCEPTED, Set.of(PICKED_UP),
			PICKED_UP, Set.of(OUT_FOR_DELIVERY, FAILED),
			OUT_FOR_DELIVERY, Set.of(DELIVERED, FAILED),
			DELIVERED, Set.of(),
			FAILED, Set.of());

	public boolean canMoveTo(DeliveryStatus next) {
		return TRANSITIONS.get(this).contains(next);
	}

	public boolean isActive() {
		return this != DELIVERED && this != FAILED;
	}
}
