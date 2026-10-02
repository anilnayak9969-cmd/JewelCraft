package com.example.JewelCraft.Security.Jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.JewelCraft.Security.UserDetailsImpl;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

	private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

	private final SecretKey key;
	private final long jwtExpirationMs;

	public JwtUtils(@Value("${spring.app.jwtSecret}") String jwtSecret,
			@Value("${spring.app.jwtExpirationMs}") long jwtExpirationMs) {
		// HS256 needs a key of at least 32 bytes - Keys.hmacShaKeyFor fails fast if it is shorter.
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		this.jwtExpirationMs = jwtExpirationMs;
	}

	public String generateToken(UserDetailsImpl user) {
		Date now = new Date();
		return Jwts.builder()
				.subject(user.getUsername())
				.claim("role", user.getRole().name())
				.issuedAt(now)
				.expiration(new Date(now.getTime() + jwtExpirationMs))
				.signWith(key)
				.compact();
	}

	/** Returns the email inside the token, or empty if the token is invalid or expired. */
	public Optional<String> getEmailIfValid(String token) {
		try {
			return Optional.ofNullable(
					Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());
		} catch (JwtException | IllegalArgumentException e) {
			log.debug("Rejected JWT: {}", e.getMessage());
			return Optional.empty();
		}
	}

	public String getJwtFromHeader(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		return null;
	}

	public long getJwtExpirationMs() {
		return jwtExpirationMs;
	}
}
