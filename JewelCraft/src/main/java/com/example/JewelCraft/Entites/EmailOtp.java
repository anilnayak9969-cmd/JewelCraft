package com.example.JewelCraft.Entites;

import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.OtpPurpose;

import jakarta.persistence.*;
import lombok.*;

/**
 * A one-time code emailed to a user. Only a BCrypt hash of the code is stored,
 * so even someone reading the database cannot see a valid code.
 */
@Entity
@Table(name = "email_otps", indexes = @Index(name = "idx_email_otps_email", columnList = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailOtp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String email;

	@Column(nullable = false)
	private String codeHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OtpPurpose purpose;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	@Builder.Default
	private int attempts = 0;

	/** True once used, or when a newer code replaced it. */
	@Builder.Default
	private boolean consumed = false;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}
}
