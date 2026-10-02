package com.example.JewelCraft.Entites;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 100)
	private String fullName;

	@Column(nullable = false, length = 15)
	private String phone;

	@Column(nullable = false)
	private String line1;

	private String line2;

	@Column(nullable = false, length = 60)
	private String city;

	@Column(nullable = false, length = 60)
	private String state;

	@Column(nullable = false, length = 10)
	private String pincode;

	@Builder.Default
	@Column(nullable = false, length = 60)
	private String country = "India";

	private boolean defaultAddress;
}
