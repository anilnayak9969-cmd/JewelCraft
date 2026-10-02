package com.example.JewelCraft.Security.Jwt;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Returns the same JSON error shape as GlobalExceptionHandler when a request is
 * rejected by the security filter chain (401 not logged in / 403 wrong role).
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint, AccessDeniedHandler {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		write(response, request, HttpServletResponse.SC_UNAUTHORIZED, "Please log in to continue");
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		write(response, request, HttpServletResponse.SC_FORBIDDEN,
				"You do not have permission to access this resource");
	}

	private void write(HttpServletResponse response, HttpServletRequest request, int status, String message)
			throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		String path = request.getRequestURI().replace("\"", "");
		response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\",\"path\":\"" + path
				+ "\",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
	}
}
