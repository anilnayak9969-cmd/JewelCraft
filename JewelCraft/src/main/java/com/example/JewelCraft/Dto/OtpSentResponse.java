package com.example.JewelCraft.Dto;

/** A new verification code was emailed. */
public record OtpSentResponse(String message, long otpExpiresInSeconds, long resendAvailableInSeconds) {
}
