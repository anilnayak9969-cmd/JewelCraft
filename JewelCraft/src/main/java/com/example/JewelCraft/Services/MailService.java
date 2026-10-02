package com.example.JewelCraft.Services;

import com.example.JewelCraft.Entites.User;

/** Account emails (verification codes, application decisions). */
public interface MailService {

	/** Sends the OTP. Throws BadRequestException if the email could not be sent. */
	void sendVerificationCode(String to, String firstName, String code, long validMinutes);

	/** Tells a staff-type applicant they were approved or rejected. Never throws. */
	void sendApprovalDecision(User user, boolean approved, String note);
}
