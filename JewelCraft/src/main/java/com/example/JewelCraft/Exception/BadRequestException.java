package com.example.JewelCraft.Exception;

/** A business rule was broken, e.g. invalid status change or expired coupon (HTTP 400). */
public class BadRequestException extends RuntimeException {

	public BadRequestException(String message) {
		super(message);
	}
}
