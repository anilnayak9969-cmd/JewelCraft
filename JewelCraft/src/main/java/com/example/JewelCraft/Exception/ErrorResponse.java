package com.example.JewelCraft.Exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
	private int status;
	private String message;
	private String path;
	private LocalDateTime timestamp;
	/** Field-level validation errors (field -> message), only for 400 validation failures. */
	private Map<String, String> errors;
	/** Machine-readable reason the frontend can react to, e.g. EMAIL_NOT_VERIFIED. Usually null. */
	private String code;

	public ErrorResponse(int status, String message, String path, LocalDateTime timestamp, Map<String, String> errors) {
		this(status, message, path, timestamp, errors, null);
	}
}
