package com.example.JewelCraft.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.AdminRegisterRequest;
import com.example.JewelCraft.Dto.AuthResponse;
import com.example.JewelCraft.Dto.CustomerRegisterRequest;
import com.example.JewelCraft.Dto.DeliveryRegisterRequest;
import com.example.JewelCraft.Dto.EmailRequest;
import com.example.JewelCraft.Dto.LoginRequest;
import com.example.JewelCraft.Dto.ManagerRegisterRequest;
import com.example.JewelCraft.Dto.MessageResponse;
import com.example.JewelCraft.Dto.OtpSentResponse;
import com.example.JewelCraft.Dto.RegistrationResponse;
import com.example.JewelCraft.Dto.StaffRegisterRequest;
import com.example.JewelCraft.Dto.UserResponse;
import com.example.JewelCraft.Dto.VerifyEmailRequest;
import com.example.JewelCraft.Dto.VerifyEmailResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.AuthService;
import com.example.JewelCraft.Services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Sign-up → email OTP → (admin approval for every role except customers) → sign in.
 * An ADMIN application stays PENDING until an existing admin approves it, so nobody can
 * grant themselves admin access just by signing up.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final UserService userService;

	/** Customer sign-up (kept at the original URL). */
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public RegistrationResponse register(@Valid @RequestBody CustomerRegisterRequest request) {
		return authService.registerCustomer(request);
	}

	@PostMapping("/register/staff")
	@ResponseStatus(HttpStatus.CREATED)
	public RegistrationResponse registerStaff(@Valid @RequestBody StaffRegisterRequest request) {
		return authService.registerStaff(request);
	}

	@PostMapping("/register/store-manager")
	@ResponseStatus(HttpStatus.CREATED)
	public RegistrationResponse registerManager(@Valid @RequestBody ManagerRegisterRequest request) {
		return authService.registerManager(request);
	}

	@PostMapping("/register/delivery-partner")
	@ResponseStatus(HttpStatus.CREATED)
	public RegistrationResponse registerDeliveryPartner(@Valid @RequestBody DeliveryRegisterRequest request) {
		return authService.registerDeliveryPartner(request);
	}

	@PostMapping("/register/admin")
	@ResponseStatus(HttpStatus.CREATED)
	public RegistrationResponse registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
		return authService.registerAdmin(request);
	}

	@PostMapping("/verify-email")
	public VerifyEmailResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
		return authService.verifyEmail(request);
	}

	@PostMapping("/resend-otp")
	public OtpSentResponse resendOtp(@Valid @RequestBody EmailRequest request) {
		return authService.resendOtp(request.getEmail());
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	/** JWTs are stateless: the client forgets the token. This endpoint exists so the API is complete. */
	@PostMapping("/logout")
	public MessageResponse logout() {
		return new MessageResponse("Logged out successfully");
	}

	@GetMapping("/me")
	public UserResponse me(@AuthenticationPrincipal UserDetailsImpl user) {
		return userService.getProfile(user.getId());
	}
}
