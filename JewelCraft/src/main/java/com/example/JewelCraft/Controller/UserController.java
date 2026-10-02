package com.example.JewelCraft.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.ChangePasswordRequest;
import com.example.JewelCraft.Dto.MessageResponse;
import com.example.JewelCraft.Dto.UpdateProfileRequest;
import com.example.JewelCraft.Dto.UserResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Profile of whoever is logged in (any role). */
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public UserResponse getProfile(@AuthenticationPrincipal UserDetailsImpl user) {
		return userService.getProfile(user.getId());
	}

	@PutMapping
	public UserResponse updateProfile(@AuthenticationPrincipal UserDetailsImpl user,
			@Valid @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(user.getId(), request);
	}

	@PutMapping("/password")
	public MessageResponse changePassword(@AuthenticationPrincipal UserDetailsImpl user,
			@Valid @RequestBody ChangePasswordRequest request) {
		userService.changePassword(user.getId(), request);
		return new MessageResponse("Password changed successfully");
	}
}
