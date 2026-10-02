package com.example.JewelCraft.Services.ServicesImpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.JewelCraft.Entites.Delivery;
import com.example.JewelCraft.Entites.Inventory;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Entites.OrderItem;
import com.example.JewelCraft.Entites.Payment;
import com.example.JewelCraft.Enum.DeliveryStatus;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;

/**
 * Side effects of the two "final" order transitions, shared by OrderService and
 * DeliveryService so stock, payment and delivery always stay consistent.
 */
@Component
class OrderLifecycle {

	/** Puts stock back, closes the payment and stops any delivery. */
	void cancel(Order order, String reason) {
		for (OrderItem item : order.getItems()) {
			if (item.getProduct() != null) {
				Inventory inv = item.getProduct().getInventory();
				if (inv != null) {
					inv.setQuantity(inv.getQuantity() + item.getQuantity());
				}
			}
		}
		order.setStatus(OrderStatus.CANCELLED);
		order.setCancelReason(reason);

		Payment payment = order.getPayment();
		if (payment != null) {
			if (payment.getStatus() == PaymentStatus.SUCCESS) {
				payment.setStatus(PaymentStatus.REFUNDED);
			} else if (payment.getStatus() == PaymentStatus.PENDING) {
				payment.setStatus(PaymentStatus.FAILED);
			}
		}
		Delivery delivery = order.getDelivery();
		if (delivery != null && delivery.getStatus().isActive()) {
			delivery.setStatus(DeliveryStatus.FAILED);
			delivery.setNotes("Order cancelled");
		}
	}

	/** Marks the order delivered; cash-on-delivery payments are now collected. */
	void markDelivered(Order order) {
		LocalDateTime now = LocalDateTime.now();
		order.setStatus(OrderStatus.DELIVERED);

		Payment payment = order.getPayment();
		if (payment != null && payment.getMethod() == PaymentMethod.COD && payment.getStatus() != PaymentStatus.SUCCESS) {
			payment.setStatus(PaymentStatus.SUCCESS);
			payment.setPaidAt(now);
		}
		Delivery delivery = order.getDelivery();
		if (delivery != null && delivery.getStatus() != DeliveryStatus.DELIVERED) {
			delivery.setStatus(DeliveryStatus.DELIVERED);
			delivery.setDeliveredAt(now);
		}
	}
}
