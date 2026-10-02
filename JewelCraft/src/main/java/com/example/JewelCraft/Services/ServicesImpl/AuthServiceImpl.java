package com.example.JewelCraft.Services.ServicesImpl;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.JewelCraft.Dto.AdminRegisterRequest;
import com.example.JewelCraft.Dto.AuthResponse;
import com.example.JewelCraft.Dto.CustomerRegisterRequest;
import com.example.JewelCraft.Dto.DeliveryRegisterRequest;
import com.example.JewelCraft.Dto.LoginRequest;
import com.example.JewelCraft.Dto.ManagerRegisterRequest;
import com.example.JewelCraft.Dto.OtpSentResponse;
import com.example.JewelCraft.Dto.RegisterRequest;
import com.example.JewelCraft.Dto.RegistrationResponse;
import com.example.JewelCraft.Dto.StaffRegisterRequest;
import com.example.JewelCraft.Dto.VerifyEmailRequest;
import com.example.JewelCraft.Dto.VerifyEmailResponse;
import com.example.JewelCraft.Entites.CustomerProfile;
import com.example.JewelCraft.Entites.DeliveryPartnerProfile;
import com.example.JewelCraft.Entites.Role;
import com.example.JewelCraft.Entites.StaffProfile;
import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Enum.ApprovalStatus;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Exception.AccountStateException;
import com.example.JewelCraft.Exception.FieldValidationException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Exception.UserAlreadyExistsException;
import com.example.JewelCraft.Mapper.UserMapper;
import com.example.JewelCraft.Repositories.CustomerProfileRepository;
import com.example.JewelCraft.Repositories.DeliveryPartnerProfileRepository;
import com.example.JewelCraft.Repositories.RoleRepository;
import com.example.JewelCraft.Repositories.StaffProfileRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Security.Jwt.JwtUtils;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.AuthService;
import com.example.JewelCraft.Services.EmailVerificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final CustomerProfileRepository customerProfileRepository;
	private final StaffProfileRepository staffProfileRepository;
	private final DeliveryPartnerProfileRepository deliveryProfileRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final UserMapper userMapper;
	private final EmailVerificationService emailVerificationService;

	// ---------------------------------------------------------------- sign-up (one per role)

	@Override
	@Transactional
	public RegistrationResponse registerCustomer(CustomerRegisterRequest req) {
		User user = createUser(req, RoleName.CUSTOMER);
		customerProfileRepository.save(CustomerProfile.builder()
				.user(user)
				.gender(req.getGender())
				.dateOfBirth(req.getDateOfBirth())
				.anniversaryDate(req.getAnniversaryDate())
				.preferredMetal(req.getPreferredMetal())
				.marketingOptIn(Boolean.TRUE.equals(req.getMarketingOptIn()))
				.build());
		return sendCode(user);
	}

	@Override
	@Transactional
	public RegistrationResponse registerStaff(StaffRegisterRequest req) {
		User user = createUser(req, RoleName.STAFF);
		staffProfileRepository.save(StaffProfile.builder()
				.user(user)
				.employeeCode(nextEmployeeCode("JC-ST-"))
				.department(req.getDepartment())
				.designation(req.getDesignation().trim())
				.storeBranch(req.getStoreBranch().trim())
				.experienceYears(req.getExperienceYears())
				.qualification(ServiceUtils.trimToNull(req.getQualification()))
				.build());
		return sendCode(user);
	}

	@Override
	@Transactional
	public RegistrationResponse registerManager(ManagerRegisterRequest req) {
		User user = createUser(req, RoleName.STORE_MANAGER);
		staffProfileRepository.save(StaffProfile.builder()
				.user(user)
				.employeeCode(nextEmployeeCode("JC-SM-"))
				.designation("Store Manager")
				.storeBranch(req.getStoreBranch().trim())
				.experienceYears(req.getExperienceYears())
				.qualification(req.getQualification().trim())
				.previousEmployer(ServiceUtils.trimToNull(req.getPreviousEmployer()))
				.build());
		return sendCode(user);
	}

	@Override
	@Transactional
	public RegistrationResponse registerDeliveryPartner(DeliveryRegisterRequest req) {
		String vehicleNumber = compact(req.getVehicleNumber());
		String licence = compact(req.getDrivingLicenseNumber());
		if (req.getVehicleType().needsLicence()) {
			Map<String, String> errors = new LinkedHashMap<>();
			if (vehicleNumber == null) {
				errors.put("vehicleNumber", "Vehicle number is required for a motor vehicle");
			}
			if (licence == null) {
				errors.put("drivingLicenseNumber", "Driving licence is required for a motor vehicle");
			}
			if (!errors.isEmpty()) {
				throw new FieldValidationException(errors);
			}
		}
		User user = createUser(req, RoleName.DELIVERY_PARTNER);
		deliveryProfileRepository.save(DeliveryPartnerProfile.builder()
				.user(user)
				.vehicleType(req.getVehicleType())
				.vehicleNumber(vehicleNumber)
				.drivingLicenseNumber(licence)
				.serviceCity(req.getServiceCity().trim())
				.servicePincode(req.getServicePincode())
				.emergencyContactName(ServiceUtils.trimToNull(req.getEmergencyContactName()))
				.emergencyContactPhone(ServiceUtils.trimToNull(req.getEmergencyContactPhone()))
				.build());
		return sendCode(user);
	}

	@Override
	@Transactional
	public RegistrationResponse registerAdmin(AdminRegisterRequest req) {
		User user = createUser(req, RoleName.ADMIN);
		staffProfileRepository.save(StaffProfile.builder()
				.user(user)
				.employeeCode(nextEmployeeCode("JC-AD-"))
				.designation(req.getDesignation().trim())
				.storeBranch(req.getOfficeLocation().trim())
				.experienceYears(req.getExperienceYears())
				.qualification(req.getQualification().trim())
				.previousEmployer(ServiceUtils.trimToNull(req.getPreviousEmployer()))
				.build());
		return sendCode(user);
	}

	/** Shared part of every sign-up: unverified user; staff-type roles also wait for approval. */
	private User createUser(RegisterRequest req, RoleName roleName) {
		String email = req.getEmail().trim().toLowerCase(Locale.ROOT);
		userRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
			if (!existing.isEmailVerified()) {
				throw new AccountStateException(AccountStateException.EMAIL_NOT_VERIFIED,
						"This email is already registered but not verified yet. Enter the code we sent, or request a new one.",
						HttpStatus.CONFLICT);
			}
			throw new UserAlreadyExistsException(email);
		});
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
		return userRepository.save(User.builder()
				.firstName(req.getFirstName().trim())
				.lastName(ServiceUtils.trimToNull(req.getLastName()))
				.email(email)
				.password(passwordEncoder.encode(req.getPassword()))
				.phone(req.getPhone())
				.role(role)
				.emailVerified(false)
				.approvalStatus(roleName == RoleName.CUSTOMER ? ApprovalStatus.APPROVED : ApprovalStatus.PENDING)
				.build());
	}

	private RegistrationResponse sendCode(User user) {
		OtpSentResponse sent = emailVerificationService.sendCode(user);
		return RegistrationResponse.builder()
				.email(user.getEmail())
				.role(user.getRole().getName())
				.message(sent.message())
				.otpExpiresInSeconds(sent.otpExpiresInSeconds())
				.resendAvailableInSeconds(sent.resendAvailableInSeconds())
				.requiresApproval(user.getApprovalStatus() == ApprovalStatus.PENDING)
				.build();
	}

	// ---------------------------------------------------------------- verification

	@Override
	@Transactional(noRollbackFor = com.example.JewelCraft.Exception.BadRequestException.class)
	public VerifyEmailResponse verifyEmail(VerifyEmailRequest req) {
		User user = emailVerificationService.verify(req.getEmail(), req.getOtp());
		if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
			return VerifyEmailResponse.builder()
					.verified(true)
					.requiresApproval(true)
					.message("Email verified. Your application is now with our team – you'll get an email once it's approved.")
					.build();
		}
		return VerifyEmailResponse.builder()
				.verified(true)
				.message("Email verified. Welcome to JewelCraft!")
				.auth(buildResponse(user))
				.build();
	}

	@Override
	public OtpSentResponse resendOtp(String email) {
		return emailVerificationService.resend(email);
	}

	// ---------------------------------------------------------------- login

	@Override
	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest req) {
		// Throws BadCredentialsException / DisabledException, mapped to 401 / 403 by GlobalExceptionHandler
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(req.getEmail().trim(), req.getPassword()));
		UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
		User user = userRepository.findById(principal.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

		// Checked only after the password is correct, so these states are not revealed to strangers.
		if (!user.isEmailVerified()) {
			throw new AccountStateException(AccountStateException.EMAIL_NOT_VERIFIED,
					"Please verify your email address to continue.");
		}
		if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
			throw new AccountStateException(AccountStateException.PENDING_APPROVAL,
					"Your application is being reviewed by our team. You'll receive an email once it's approved.");
		}
		if (user.getApprovalStatus() == ApprovalStatus.REJECTED) {
			throw new AccountStateException(AccountStateException.REJECTED,
					"Your application was not approved. Please contact care@jewelcraft.com for details.");
		}
		return buildResponse(user);
	}

	private AuthResponse buildResponse(User user) {
		String token = jwtUtils.generateToken(new UserDetailsImpl(user));
		return new AuthResponse(token, "Bearer", jwtUtils.getJwtExpirationMs(), userMapper.toResponse(user));
	}

	// ---------------------------------------------------------------- helpers

	private String nextEmployeeCode(String prefix) {
		long n = staffProfileRepository.count() + 1;
		String code;
		do {
			code = prefix + String.format("%04d", n++);
		} while (staffProfileRepository.existsByEmployeeCode(code));
		return code;
	}

	/** "mh 12-ab 1234" -> "MH12AB1234"; blank -> null. */
	private static String compact(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		return value.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
	}
}
