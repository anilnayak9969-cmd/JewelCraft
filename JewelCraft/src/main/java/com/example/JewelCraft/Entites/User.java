package com.example.JewelCraft.Entites;

import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.ApprovalStatus;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String firstName;

	@Column(length = 50)
	private String lastName;

	@Column(nullable = false, unique = true, length = 120)
	private String email;

	/** BCrypt hash - never the plain password. */
	@Column(nullable = false)
	private String password;

	@Column(length = 15)
	private String phone;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Builder.Default
	private boolean enabled = true;

	/**
	 * Self-registered accounts start unverified until the emailed OTP is entered.
	 * The DB default of 1 keeps accounts that existed before this column was added working.
	 */
	@Builder.Default
	@Column(nullable = false, columnDefinition = "bit(1) not null default b'1'")
	private boolean emailVerified = true;

	/** Staff / store manager / delivery sign-ups wait for an admin; customers are APPROVED straight away. */
	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) not null default 'APPROVED'")
	private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public String getFullName() {
		return lastName == null || lastName.isBlank() ? firstName : firstName + " " + lastName;
	}
}
