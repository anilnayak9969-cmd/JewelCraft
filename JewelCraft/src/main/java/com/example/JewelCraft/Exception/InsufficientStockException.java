package com.example.JewelCraft.Exception;

public class InsufficientStockException extends RuntimeException {

	public InsufficientStockException(String productName, int available) {
		super(available <= 0 ? productName + " is out of stock"
				: "Only " + available + " left in stock for " + productName);
	}
}
