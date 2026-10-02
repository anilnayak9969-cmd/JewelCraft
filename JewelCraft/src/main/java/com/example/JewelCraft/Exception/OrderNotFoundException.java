package com.example.JewelCraft.Exception;

public class OrderNotFoundException extends ResourceNotFoundException {

	public OrderNotFoundException(Long orderId) {
		super("Order", "id", orderId);
	}
}
