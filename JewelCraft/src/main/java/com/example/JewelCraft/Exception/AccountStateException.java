package com.example.JewelCraft.Exception;

import org.springframework.http.HttpStatus;

/**
 * The account exists but cannot be used yet (or any more). The code tells the frontend what to do:
 * EMAIL_NOT_VERIFIED -> open the OTP screen, PENDING_APPROVAL / REJECTED -> show the status.
 */
public class AccountStateException extends RuntimeException {

	public static final String EMAIL_NOT_VERIFIED = "EMAIL_NOT_VERIFIED";
	public static final String PENDING_APPROVAL = "PENDING_APPROVAL";
	public static final String REJECTED = "APPLICATION_REJECTED";

	private final String code;
	private final HttpStatus status;

	public AccountStateException(String code, String message, HttpStatus status) {
		super(message);
		this.code = code;
		this.status = status;
	}

	public AccountStateException(String code, String message) {
		this(code, message, HttpStatus.FORBIDDEN);
	}

	public String getCode() {
		return code;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
