package com.example.JewelCraft.Exception;

/** The user is logged in but is not allowed to touch this resource (HTTP 403). */
public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException(String message) {
		super(message);
	}
}
