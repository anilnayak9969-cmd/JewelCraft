package com.example.JewelCraft.Services.ServicesImpl;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Services.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Sends HTML emails through the SMTP server in application.properties (Gmail).
 * If no mail username is configured, emails are skipped and logged instead, so the
 * app still runs on a machine without mail settings.
 */
@Service
public class MailServiceImpl implements MailService {

	private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

	private final ObjectProvider<JavaMailSender> mailSenderProvider;
	private final String fromAddress;
	private final String fromName;
	private final boolean logCodes;

	public MailServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider,
			@Value("${spring.mail.username:}") String fromAddress,
			@Value("${app.mail.from-name:JewelCraft}") String fromName,
			@Value("${app.otp.log-code:false}") boolean logCodes) {
		this.mailSenderProvider = mailSenderProvider;
		this.fromAddress = fromAddress;
		this.fromName = fromName;
		this.logCodes = logCodes;
	}

	private boolean enabled() {
		return StringUtils.hasText(fromAddress) && mailSenderProvider.getIfAvailable() != null;
	}

	/** Checks the SMTP login once at start-up (in the background) so a wrong app password shows up early. */
	@EventListener(ApplicationReadyEvent.class)
	public void checkConnection() {
		if (!enabled()) {
			log.warn("Email is not configured (spring.mail.username is empty) - verification codes will only be logged.");
			return;
		}
		if (mailSenderProvider.getIfAvailable() instanceof JavaMailSenderImpl impl) {
			Thread check = new Thread(() -> {
				try {
					impl.testConnection();
					log.info("Mail server connection OK ({} as {})", impl.getHost(), fromAddress);
				} catch (MessagingException e) {
					log.warn("Cannot log in to the mail server as {}: {}. Check MAIL_USERNAME / MAIL_PASSWORD (Gmail app password).",
							fromAddress, e.getMessage());
				}
			}, "mail-check");
			check.setDaemon(true);
			check.start();
		}
	}

	@Override
	public void sendVerificationCode(String to, String firstName, String code, long validMinutes) {
		if (logCodes || !enabled()) {
			log.info("Verification code for {}: {}", to, code);
		}
		if (!enabled()) {
			return;
		}
		String digits = String.join(" ", code.split(""));
		String body = """
				<p style="margin:0 0 16px">Dear %s,</p>
				<p style="margin:0 0 24px">Welcome to JewelCraft. Please use the code below to verify your email address.</p>
				<div style="text-align:center;margin:28px 0">
				  <div style="display:inline-block;border:1px solid #c9a96e;padding:18px 32px;font-family:Georgia,serif;
				              font-size:34px;letter-spacing:8px;color:#14110f">%s</div>
				</div>
				<p style="margin:0 0 8px;color:#5e564e">This code expires in %d minutes. Never share it with anyone –
				   JewelCraft will never ask you for it.</p>
				<p style="margin:0;color:#8c847b;font-size:13px">If you did not create an account, you can ignore this email.</p>
				""".formatted(HtmlUtils.htmlEscape(firstName), digits, validMinutes);
		try {
			send(to, code + " is your JewelCraft verification code", "Verify your email", body);
		} catch (MailException | MessagingException e) {
			log.error("Could not send verification email to {}: {}", to, e.getMessage());
			throw new BadRequestException("We could not send the verification email. Please check the address and try again.");
		}
	}

	@Override
	public void sendApprovalDecision(User user, boolean approved, String note) {
		if (!enabled()) {
			log.info("Application for {} was {}", user.getEmail(), approved ? "approved" : "rejected");
			return;
		}
		String extra = StringUtils.hasText(note)
				? "<p style=\"margin:16px 0 0;padding:14px 16px;background:#f4eee4\">" + HtmlUtils.htmlEscape(note) + "</p>"
				: "";
		String body = approved
				? """
						<p style="margin:0 0 16px">Dear %s,</p>
						<p style="margin:0">Your JewelCraft account has been <b>approved</b>. You can now sign in and start working
						from your dashboard.</p>%s
						""".formatted(HtmlUtils.htmlEscape(user.getFirstName()), extra)
				: """
						<p style="margin:0 0 16px">Dear %s,</p>
						<p style="margin:0">Thank you for applying to JewelCraft. We are unable to approve your account at this time.</p>%s
						""".formatted(HtmlUtils.htmlEscape(user.getFirstName()), extra);
		try {
			send(user.getEmail(), approved ? "Your JewelCraft account is approved" : "Your JewelCraft application",
					approved ? "Welcome aboard" : "Application update", body);
		} catch (MailException | MessagingException e) {
			log.warn("Could not send approval email to {}: {}", user.getEmail(), e.getMessage());
		}
	}

	private void send(String to, String subject, String heading, String innerHtml) throws MessagingException {
		JavaMailSender sender = mailSenderProvider.getObject();
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
		try {
			helper.setFrom(fromAddress, fromName);
		} catch (java.io.UnsupportedEncodingException e) {
			helper.setFrom(fromAddress);
		}
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(layout(heading, innerHtml), true);
		sender.send(message);
	}

	/** Luxury-styled email shell matching the website (noir header, gold accents). */
	private static String layout(String heading, String innerHtml) {
		return """
				<!doctype html><html><body style="margin:0;background:#fbf8f3;font-family:Helvetica,Arial,sans-serif;color:#1c1815">
				<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#fbf8f3;padding:32px 12px">
				<tr><td align="center">
				  <table role="presentation" width="560" cellpadding="0" cellspacing="0" style="max-width:560px;width:100%%;background:#ffffff;border:1px solid #e6ded2">
				    <tr><td style="background:#14110f;padding:28px;text-align:center">
				      <div style="font-family:Georgia,serif;font-size:26px;letter-spacing:9px;color:#f4ecdf">JEWELCRAFT</div>
				      <div style="font-size:10px;letter-spacing:5px;color:#c9a96e;margin-top:6px">FINE JEWELLERY</div>
				    </td></tr>
				    <tr><td style="padding:36px 36px 12px">
				      <div style="font-size:11px;letter-spacing:4px;color:#a8834a;text-transform:uppercase;margin-bottom:10px">%s</div>
				      <div style="font-size:15px;line-height:1.6">%s</div>
				    </td></tr>
				    <tr><td style="padding:24px 36px 32px;border-top:1px solid #efe9df;font-size:12px;color:#8c847b">
				      JewelCraft Atelier · MG Road, Bengaluru · care@jewelcraft.com
				    </td></tr>
				  </table>
				</td></tr></table></body></html>
				""".formatted(heading, innerHtml);
	}
}
