package com.example.JewelCraft.Security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.JewelCraft.Security.Jwt.AuthEntryPointJwt;
import com.example.JewelCraft.Security.Jwt.AuthTokenFilter;

import lombok.RequiredArgsConstructor;

/**
 * URL-level rules live here; finer rules (e.g. only STORE_MANAGER/ADMIN may delete a
 * product) are @PreAuthorize annotations on the controllers.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private static final String ADMIN = "ADMIN";
	private static final String STORE_MANAGER = "STORE_MANAGER";
	private static final String STAFF = "STAFF";
	private static final String DELIVERY_PARTNER = "DELIVERY_PARTNER";
	private static final String CUSTOMER = "CUSTOMER";

	private final UserDetailsServiceImpl userDetailsService;
	private final AuthEntryPointJwt authEntryPoint;
	private final AuthTokenFilter authTokenFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(provider);
	}

	/** The JWT filter runs inside the security chain only, not a second time as a plain servlet filter. */
	@Bean
	public FilterRegistrationBean<AuthTokenFilter> authTokenFilterRegistration(AuthTokenFilter filter) {
		FilterRegistrationBean<AuthTokenFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(e -> e.authenticationEntryPoint(authEntryPoint).accessDeniedHandler(authEntryPoint))
				.authorizeHttpRequests(auth -> auth
						// public
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/error", "/uploads/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/register/*", "/api/auth/login",
								"/api/auth/logout", "/api/auth/verify-email", "/api/auth/resend-otp")
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/api/products/manage", "/api/products/manage/**")
						.hasAnyRole(STAFF, STORE_MANAGER, ADMIN)
						.requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/api/metal-rates/**",
								"/api/offers/active", "/api/reviews/latest")
						.permitAll()
						// role areas
						.requestMatchers("/api/admin/**").hasRole(ADMIN)
						.requestMatchers("/api/store/**").hasAnyRole(STORE_MANAGER, ADMIN)
						.requestMatchers("/api/staff/**").hasAnyRole(STAFF, STORE_MANAGER, ADMIN)
						.requestMatchers("/api/delivery/**").hasAnyRole(DELIVERY_PARTNER, ADMIN)
						.requestMatchers("/api/cart/**", "/api/wishlist/**", "/api/addresses/**").hasRole(CUSTOMER)
						.requestMatchers("/api/inventory/**").hasAnyRole(STAFF, STORE_MANAGER, ADMIN)
						// everything else needs a login; controllers narrow it further
						.anyRequest().authenticated())
				.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
