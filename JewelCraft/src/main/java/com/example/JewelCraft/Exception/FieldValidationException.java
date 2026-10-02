package com.example.JewelCraft.Exception;

import java.util.Map;

/** A validation rule that depends on other fields (e.g. licence needed unless the vehicle is a bicycle). */
public class FieldValidationException extends RuntimeException {

	private final Map<String, String> errors;

	public FieldValidationException(Map<String, String> errors) {
		super("Validation failed");
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
