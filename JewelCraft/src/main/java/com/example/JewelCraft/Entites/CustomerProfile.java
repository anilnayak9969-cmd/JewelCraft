package com.example.JewelCraft.Entites;

import java.time.LocalDate;

import com.example.JewelCraft.Enum.Gender;
import com.example.JewelCraft.Enum.MetalType;

import jakarta.persistence.*;
import lombok.*;

/** Extra details a customer gives at sign-up (birthday & anniversary are used for occasion offers). */
@Entity
@Table(name = "customer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private Gender gender;

	private LocalDate dateOfBirth;

	private LocalDate anniversaryDate;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private MetalType preferredMetal;

	@Builder.Default
	private boolean marketingOptIn = false;
}
