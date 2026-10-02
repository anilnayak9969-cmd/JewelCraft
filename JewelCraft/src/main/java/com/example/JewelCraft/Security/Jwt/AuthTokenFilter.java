package com.example.JewelCraft.Security.Jwt;

import java.io.IOException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Security.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Reads "Authorization: Bearer <token>" on every request. The user is re-loaded
 * from the database so disabled accounts or changed roles take effect immediately.
 */
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String jwt = jwtUtils.getJwtFromHeader(request);
		if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			jwtUtils.getEmailIfValid(jwt).ifPresent(email -> authenticate(email, request));
		}
		chain.doFilter(request, response);
	}

	private void authenticate(String email, HttpServletRequest request) {
		try {
			UserDetailsImpl user = userDetailsService.loadUserByUsername(email);
			if (!user.isEnabled()) {
				return;
			}
			var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (UsernameNotFoundException e) {
			// user was deleted after the token was issued - treat as anonymous
		}
	}
}
