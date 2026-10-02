package com.example.JewelCraft.Enum;

import java.util.Map;
import java.util.Set;

public enum OrderStatus {
	PLACED, CONFIRMED, PROCESSING, PACKED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED;

	private static final Map<OrderStatus, Set<OrderStatus>> TRANSITIONS = Map.of(
			PLACED, Set.of(CONFIRMED, CANCELLED),
			CONFIRMED, Set.of(PROCESSING, CANCELLED),
			PROCESSING, Set.of(PACKED, CANCELLED),
			PACKED, Set.of(SHIPPED, CANCELLED),
			SHIPPED, Set.of(OUT_FOR_DELIVERY),
			OUT_FOR_DELIVERY, Set.of(DELIVERED),
			DELIVERED, Set.of(),
			CANCELLED, Set.of());

	public boolean canMoveTo(OrderStatus next) {
		return TRANSITIONS.get(this).contains(next);
	}

	/** Customers may cancel only before the order is being prepared. */
	public boolean isCustomerCancellable() {
		return this == PLACED || this == CONFIRMED;
	}
}
