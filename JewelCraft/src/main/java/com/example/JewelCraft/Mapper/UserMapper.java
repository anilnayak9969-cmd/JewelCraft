package com.example.JewelCraft.Mapper;

import org.springframework.stereotype.Component;

import com.example.JewelCraft.Dto.AddressResponse;
import com.example.JewelCraft.Dto.UserProfileDetails;
import com.example.JewelCraft.Dto.UserResponse;
import com.example.JewelCraft.Entites.Address;
import com.example.JewelCraft.Entites.ShippingAddress;
import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Repositories.CustomerProfileRepository;
import com.example.JewelCraft.Repositories.DeliveryPartnerProfileRepository;
import com.example.JewelCraft.Repositories.StaffProfileRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

	private final CustomerProfileRepository customerProfileRepository;
	private final StaffProfileRepository staffProfileRepository;
	private final DeliveryPartnerProfileRepository deliveryProfileRepository;

	/** For lists: no role profile (avoids an extra query per row). */
	public UserResponse toResponse(User u) {
		return UserResponse.builder()
				.id(u.getId())
				.firstName(u.getFirstName())
				.lastName(u.getLastName())
				.fullName(u.getFullName())
				.email(u.getEmail())
				.phone(u.getPhone())
				.role(u.getRole().getName())
				.enabled(u.isEnabled())
				.emailVerified(u.isEmailVerified())
				.approvalStatus(u.getApprovalStatus())
				.createdAt(u.getCreatedAt())
				.build();
	}

	/** For a single user (own profile, admin user detail): includes the role-specific sign-up details. */
	public UserResponse toDetailedResponse(User u) {
		UserResponse res = toResponse(u);
		res.setProfile(profileOf(u));
		return res;
	}

	private UserProfileDetails profileOf(User u) {
		return switch (u.getRole().getName()) {
			case CUSTOMER -> customerProfileRepository.findByUserId(u.getId())
					.map(p -> UserProfileDetails.builder()
							.gender(p.getGender())
							.dateOfBirth(p.getDateOfBirth())
							.anniversaryDate(p.getAnniversaryDate())
							.preferredMetal(p.getPreferredMetal())
							.marketingOptIn(p.isMarketingOptIn())
							.build())
					.orElse(null);
			case STAFF, STORE_MANAGER, ADMIN -> staffProfileRepository.findByUserId(u.getId())
					.map(p -> UserProfileDetails.builder()
							.employeeCode(p.getEmployeeCode())
							.department(p.getDepartment())
							.designation(p.getDesignation())
							.storeBranch(p.getStoreBranch())
							.experienceYears(p.getExperienceYears())
							.qualification(p.getQualification())
							.previousEmployer(p.getPreviousEmployer())
							.build())
					.orElse(null);
			case DELIVERY_PARTNER -> deliveryProfileRepository.findByUserId(u.getId())
					.map(p -> UserProfileDetails.builder()
							.vehicleType(p.getVehicleType())
							.vehicleNumber(p.getVehicleNumber())
							.drivingLicenseNumber(p.getDrivingLicenseNumber())
							.serviceCity(p.getServiceCity())
							.servicePincode(p.getServicePincode())
							.emergencyContactName(p.getEmergencyContactName())
							.emergencyContactPhone(p.getEmergencyContactPhone())
							.build())
					.orElse(null);
		};
	}

	public AddressResponse toResponse(Address a) {
		return AddressResponse.builder()
				.id(a.getId())
				.fullName(a.getFullName())
				.phone(a.getPhone())
				.line1(a.getLine1())
				.line2(a.getLine2())
				.city(a.getCity())
				.state(a.getState())
				.pincode(a.getPincode())
				.country(a.getCountry())
				.defaultAddress(a.isDefaultAddress())
				.build();
	}

	public AddressResponse toResponse(ShippingAddress s) {
		if (s == null) {
			return null;
		}
		return AddressResponse.builder()
				.fullName(s.getShipFullName())
				.phone(s.getShipPhone())
				.line1(s.getShipLine1())
				.line2(s.getShipLine2())
				.city(s.getShipCity())
				.state(s.getShipState())
				.pincode(s.getShipPincode())
				.country(s.getShipCountry())
				.build();
	}
}
