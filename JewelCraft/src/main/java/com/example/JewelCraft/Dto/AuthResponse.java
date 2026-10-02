package com.example.JewelCraft.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	private String token;
	private String tokenType;
	private long expiresInMs;
	private UserResponse user;
}
