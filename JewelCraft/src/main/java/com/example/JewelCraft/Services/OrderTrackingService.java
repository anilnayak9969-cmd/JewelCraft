package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.TrackingEventResponse;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Enum.TrackingEvent;
import com.example.JewelCraft.Security.UserDetailsImpl;

/** Writes and reads the step-by-step tracking history shown to customers. */
public interface OrderTrackingService {

	/** message may be null – a friendly default is used. actor may be null for system events. */
	void record(Order order, TrackingEvent event, String message, UserDetailsImpl actor);

	/** Cancellation (+ refund entry when the order had been paid online). */
	void recordCancellation(Order order, boolean wasPaid, String reason, UserDetailsImpl actor);

	List<TrackingEventResponse> history(Long orderId);

	/** Gives orders created before tracking existed a starting history. Returns how many were filled. */
	int backfillMissing();
}
