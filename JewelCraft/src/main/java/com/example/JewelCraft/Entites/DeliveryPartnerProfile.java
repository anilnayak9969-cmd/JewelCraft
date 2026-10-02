package com.example.JewelCraft.Entites;

import com.example.JewelCraft.Enum.VehicleType;

import jakarta.persistence.*;
import lombok.*;

/** Vehicle and service-area details for DELIVERY_PARTNER accounts. */
@Entity
@Table(name = "delivery_partner_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPartnerProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private VehicleType vehicleType;

	/** Stored upper-case without spaces, e.g. MH12AB1234. Empty for bicycles. */
	@Column(length = 15)
	private String vehicleNumber;

	@Column(length = 20)
	private String drivingLicenseNumber;

	@Column(nullable = false, length = 60)
	private String serviceCity;

	@Column(nullable = false, length = 6)
	private String servicePincode;

	@Column(length = 100)
	private String emergencyContactName;

	@Column(length = 15)
	private String emergencyContactPhone;
}
