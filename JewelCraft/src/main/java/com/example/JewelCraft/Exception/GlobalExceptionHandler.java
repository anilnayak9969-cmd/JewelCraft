package com.example.JewelCraft.Exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> userExists(UserAlreadyExistsException ex, HttpServletRequest req) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), req);
	}

	@ExceptionHandler({ InvalidCredentialsException.class, BadCredentialsException.class })
	public ResponseEntity<ErrorResponse> badCredentials(RuntimeException ex, HttpServletRequest req) {
		return build(HttpStatus.UNAUTHORIZED, "Invalid email or password", req);
	}

	@ExceptionHandler(FieldValidationException.class)
	public ResponseEntity<ErrorResponse> fieldValidation(FieldValidationException ex, HttpServletRequest req) {
		return ResponseEntity.badRequest().body(
				new ErrorResponse(400, "Validation failed", req.getRequestURI(), LocalDateTime.now(), ex.getErrors()));
	}

	@ExceptionHandler(AccountStateException.class)
	public ResponseEntity<ErrorResponse> accountState(AccountStateException ex, HttpServletRequest req) {
		return ResponseEntity.status(ex.getStatus()).body(new ErrorResponse(ex.getStatus().value(), ex.getMessage(),
				req.getRequestURI(), LocalDateTime.now(), null, ex.getCode()));
	}

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ErrorResponse> disabled(DisabledException ex, HttpServletRequest req) {
		return build(HttpStatus.FORBIDDEN, "Your account has been disabled. Please contact support.", req);
	}

	@ExceptionHandler({ UnauthorizedException.class, AccessDeniedException.class })
	public ResponseEntity<ErrorResponse> forbidden(RuntimeException ex, HttpServletRequest req) {
		String msg = ex instanceof UnauthorizedException ? ex.getMessage()
				: "You do not have permission to perform this action";
		return build(HttpStatus.FORBIDDEN, msg, req);
	}

	@ExceptionHandler({ InsufficientStockException.class, BadRequestException.class })
	public ResponseEntity<ErrorResponse> badRequest(RuntimeException ex, HttpServletRequest req) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		Map<String, String> errors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
		ErrorResponse body = new ErrorResponse(400, "Validation failed", req.getRequestURI(), LocalDateTime.now(),
				errors);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class, MissingServletRequestPartException.class })
	public ResponseEntity<ErrorResponse> malformed(Exception ex, HttpServletRequest req) {
		return build(HttpStatus.BAD_REQUEST, "Invalid request: please check the values you sent", req);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> integrity(DataIntegrityViolationException ex, HttpServletRequest req) {
		log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
		return build(HttpStatus.CONFLICT, "This record is linked to other data or already exists", req);
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<ErrorResponse> concurrent(ObjectOptimisticLockingFailureException ex,
			HttpServletRequest req) {
		return build(HttpStatus.CONFLICT, "The item was updated by someone else. Please try again.", req);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> tooLarge(MaxUploadSizeExceededException ex, HttpServletRequest req) {
		return build(HttpStatus.PAYLOAD_TOO_LARGE, "File is too large (max 10MB)", req);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> noResource(NoResourceFoundException ex, HttpServletRequest req) {
		return build(HttpStatus.NOT_FOUND, "Resource not found", req);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> generic(Exception ex, HttpServletRequest req) {
		log.error("Unhandled error on {}", req.getRequestURI(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.", req);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
		return ResponseEntity.status(status)
				.body(new ErrorResponse(status.value(), message, req.getRequestURI(), LocalDateTime.now(), null));
	}
}
