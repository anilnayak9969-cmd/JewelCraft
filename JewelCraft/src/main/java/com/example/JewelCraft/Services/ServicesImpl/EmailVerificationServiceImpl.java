package com.example.JewelCraft.Services.ServicesImpl;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.OtpSentResponse;
import com.example.JewelCraft.Entites.EmailOtp;
import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Enum.OtpPurpose;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Repositories.EmailOtpRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Services.EmailVerificationService;
import com.example.JewelCraft.Services.MailService;

import lombok.RequiredArgsConstructor;

/**
 * 6-digit codes, valid for 10 minutes, stored only as BCrypt hashes.
 * Limits: 60 s between sends, 5 sends per hour, 5 wrong tries per code.
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

	static final Duration VALIDITY = Duration.ofMinutes(10);
	static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
	static final int MAX_ATTEMPTS = 5;
	static final int MAX_SENDS_PER_HOUR = 5;
	private static final OtpPurpose PURPOSE = OtpPurpose.EMAIL_VERIFICATION;

	private final EmailOtpRepository otpRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private final SecureRandom random = new SecureRandom();

	@Override
	@Transactional
	public OtpSentResponse sendCode(User user) {
		String email = user.getEmail();
		LocalDateTime now = LocalDateTime.now();

		otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, PURPOSE).ifPresent(last -> {
			long wait = RESEND_COOLDOWN.minus(Duration.between(last.getCreatedAt(), now)).toSeconds();
			if (wait > 0) {
				throw new BadRequestException("Please wait " + wait + " seconds before requesting a new code");
			}
		});
		if (otpRepository.countByEmailAndPurposeAndCreatedAtAfter(email, PURPOSE, now.minusHours(1)) >= MAX_SENDS_PER_HOUR) {
			throw new BadRequestException("Too many codes requested. Please try again in an hour.");
		}

		// Only the newest code is valid.
		otpRepository.findByEmailAndPurposeAndConsumedFalse(email, PURPOSE).forEach(o -> o.setConsumed(true));

		String code = String.format("%06d", random.nextInt(1_000_000));
		otpRepository.save(EmailOtp.builder()
				.email(email)
				.codeHash(passwordEncoder.encode(code))
				.purpose(PURPOSE)
				.expiresAt(now.plus(VALIDITY))
				.createdAt(now)
				.build());

		// If sending fails this throws, and the whole transaction (including a new registration) rolls back.
		mailService.sendVerificationCode(email, user.getFirstName(), code, VALIDITY.toMinutes());
		return new OtpSentResponse("We've sent a 6-digit code to " + mask(email), VALIDITY.toSeconds(),
				RESEND_COOLDOWN.toSeconds());
	}

	@Override
	@Transactional
	public OtpSentResponse resend(String email) {
		User user = userRepository.findByEmailIgnoreCase(email.trim())
				.orElseThrow(() -> new BadRequestException("No account found for this email. Please register first."));
		if (user.isEmailVerified()) {
			throw new BadRequestException("This email is already verified. Please sign in.");
		}
		return sendCode(user);
	}

	/** Wrong attempts must be saved even though we throw, hence noRollbackFor. */
	@Override
	@Transactional(noRollbackFor = BadRequestException.class)
	public User verify(String email, String code) {
		BadRequestException invalid = new BadRequestException("This code is invalid or has expired. Please request a new one.");
		User user = userRepository.findByEmailIgnoreCase(email.trim()).orElseThrow(() -> invalid);
		if (user.isEmailVerified()) {
			throw new BadRequestException("This email is already verified. Please sign in.");
		}
		EmailOtp otp = otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(user.getEmail(), PURPOSE)
				.orElseThrow(() -> invalid);
		if (otp.isConsumed() || otp.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw invalid;
		}
		if (otp.getAttempts() >= MAX_ATTEMPTS) {
			throw new BadRequestException("Too many incorrect attempts. Please request a new code.");
		}
		if (!passwordEncoder.matches(code, otp.getCodeHash())) {
			otp.setAttempts(otp.getAttempts() + 1);
			int left = MAX_ATTEMPTS - otp.getAttempts();
			throw new BadRequestException(left > 0
					? "Incorrect code. " + left + (left == 1 ? " attempt" : " attempts") + " left."
					: "Too many incorrect attempts. Please request a new code.");
		}
		otp.setConsumed(true);
		user.setEmailVerified(true);
		return user;
	}

	/** anilnayak9969@gmail.com -> an*********69@gmail.com */
	static String mask(String email) {
		int at = email.indexOf('@');
		if (at <= 2) {
			return email;
		}
		String name = email.substring(0, at);
		int keepEnd = name.length() > 5 ? 2 : 0;
		return name.substring(0, 2) + "*".repeat(name.length() - 2 - keepEnd) + name.substring(name.length() - keepEnd)
				+ email.substring(at);
	}
}
