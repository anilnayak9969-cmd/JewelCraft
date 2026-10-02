package com.example.JewelCraft.Services;

import com.example.JewelCraft.Dto.OtpSentResponse;
import com.example.JewelCraft.Entites.User;

/** Email OTP: issue, resend and check one-time verification codes. */
public interface EmailVerificationService {

	/** Emails a fresh code to the user (replacing any earlier one). */
	OtpSentResponse sendCode(User user);

	/** Resend for an unverified account. */
	OtpSentResponse resend(String email);

	/** Checks the code; on success marks the email verified and returns the user. */
	User verify(String email, String code);
}
