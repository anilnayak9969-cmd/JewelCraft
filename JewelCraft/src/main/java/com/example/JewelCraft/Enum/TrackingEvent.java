package com.example.JewelCraft.Enum;

/** One step in an order's live tracking history. */
public enum TrackingEvent {
	ORDER_PLACED("Order placed"),
	PAYMENT_RECEIVED("Payment received"),
	CONFIRMED("Order confirmed"),
	PROCESSING("Being crafted & checked"),
	PACKED("Packed securely"),
	DELIVERY_ASSIGNED("Delivery partner assigned"),
	DELIVERY_ACCEPTED("Delivery partner on the way to store"),
	SHIPPED("Picked up – in transit"),
	OUT_FOR_DELIVERY("Out for delivery"),
	DELIVERED("Delivered"),
	DELIVERY_FAILED("Delivery attempt failed"),
	CANCELLED("Order cancelled"),
	PAYMENT_REFUNDED("Payment refunded"),
	/** A message from the store that doesn't change the order's status (e.g. a delay). */
	STORE_UPDATE("Update from JewelCraft");

	private final String title;

	TrackingEvent(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	/** The event recorded when an order moves to this status. */
	public static TrackingEvent forStatus(OrderStatus status) {
		return switch (status) {
			case PLACED -> ORDER_PLACED;
			case CONFIRMED -> CONFIRMED;
			case PROCESSING -> PROCESSING;
			case PACKED -> PACKED;
			case SHIPPED -> SHIPPED;
			case OUT_FOR_DELIVERY -> OUT_FOR_DELIVERY;
			case DELIVERED -> DELIVERED;
			case CANCELLED -> CANCELLED;
		};
	}
}
