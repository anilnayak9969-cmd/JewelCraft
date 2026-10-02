package com.example.JewelCraft.Exception;

public class UserAlreadyExistsException extends RuntimeException {

	public UserAlreadyExistsException(String email) {
		super("An account with email " + email + " already exists");
	}
}
