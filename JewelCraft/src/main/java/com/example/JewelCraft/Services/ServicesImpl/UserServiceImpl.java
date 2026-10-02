package com.example.JewelCraft.Services.ServicesImpl;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.JewelCraft.Dto.AdminUserRequest;
import com.example.JewelCraft.Dto.ApprovalRequest;
import com.example.JewelCraft.Dto.ChangePasswordRequest;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.UpdateProfileRequest;
import com.example.JewelCraft.Dto.UserResponse;
import com.example.JewelCraft.Entites.Role;
import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Enum.ApprovalStatus;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Exception.UserAlreadyExistsException;
import com.example.JewelCraft.Mapper.UserMapper;
import com.example.JewelCraft.Repositories.AddressRepository;
import com.example.JewelCraft.Repositories.CartRepository;
import com.example.JewelCraft.Repositories.CustomerProfileRepository;
import com.example.JewelCraft.Repositories.CustomerRequestRepository;
import com.example.JewelCraft.Repositories.DeliveryPartnerProfileRepository;
import com.example.JewelCraft.Repositories.EmailOtpRepository;
import com.example.JewelCraft.Repositories.DeliveryRepository;
import com.example.JewelCraft.Repositories.OrderRepository;
import com.example.JewelCraft.Repositories.ReviewRepository;
import com.example.JewelCraft.Repositories.RoleRepository;
import com.example.JewelCraft.Repositories.StaffProfileRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Repositories.WishlistRepository;
import com.example.JewelCraft.Services.MailService;
import com.example.JewelCraft.Services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final OrderRepository orderRepository;
	private final DeliveryRepository deliveryRepository;
	private final AddressRepository addressRepository;
	private final CartRepository cartRepository;
	private final WishlistRepository wishlistRepository;
	private final ReviewRepository reviewRepository;
	private final CustomerRequestRepository customerRequestRepository;
	private final CustomerProfileRepository customerProfileRepository;
	private final StaffProfileRepository staffProfileRepository;
	private final DeliveryPartnerProfileRepository deliveryProfileRepository;
	private final EmailOtpRepository emailOtpRepository;
	private final MailService mailService;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;

	@Override
	@Transactional(readOnly = true)
	public UserResponse getProfile(Long userId) {
		return userMapper.toDetailedResponse(findUser(userId));
	}

	@Override
	@Transactional
	public UserResponse updateProfile(Long userId, UpdateProfileRequest req) {
		User user = findUser(userId);
		user.setFirstName(req.getFirstName().trim());
		user.setLastName(ServiceUtils.trimToNull(req.getLastName()));
		user.setPhone(req.getPhone());
		return userMapper.toDetailedResponse(user);
	}

	@Override
	@Transactional
	public void changePassword(Long userId, ChangePasswordRequest req) {
		User user = findUser(userId);
		if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
			throw new BadRequestException("Current password is incorrect");
		}
		user.setPassword(passwordEncoder.encode(req.getNewPassword()));
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<UserResponse> searchUsers(RoleName role, ApprovalStatus approval, String search, int page,
			int size) {
		var pageable = ServiceUtils.page(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return PageResponse.of(userRepository.search(role, approval, ServiceUtils.likePattern(search), pageable),
				userMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUser(Long id) {
		return userMapper.toDetailedResponse(findUser(id));
	}

	@Override
	@Transactional
	public UserResponse decideApproval(Long adminId, Long id, ApprovalRequest req) {
		if (Objects.equals(adminId, id)) {
			throw new BadRequestException("You cannot change the approval of your own account");
		}
		if (req.getStatus() == ApprovalStatus.PENDING) {
			throw new BadRequestException("Choose Approve or Reject");
		}
		User user = findUser(id);
		if (user.getRole().getName() == RoleName.CUSTOMER) {
			throw new BadRequestException("Customer accounts do not need approval");
		}
		// Admins can't use approval to lock out an admin who is already approved, only decide on an application.
		if (user.getRole().getName() == RoleName.ADMIN && user.getApprovalStatus() == ApprovalStatus.APPROVED) {
			throw new BadRequestException("This admin account is already approved");
		}
		boolean approved = req.getStatus() == ApprovalStatus.APPROVED;
		user.setApprovalStatus(req.getStatus());
		if (approved) {
			user.setEnabled(true);
		}
		mailService.sendApprovalDecision(user, approved, ServiceUtils.trimToNull(req.getNote()));
		return userMapper.toDetailedResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public long countPendingApprovals() {
		return userRepository.countByApprovalStatus(ApprovalStatus.PENDING);
	}

	@Override
	@Transactional
	public UserResponse createUser(AdminUserRequest req) {
		String email = req.getEmail().trim().toLowerCase();
		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new UserAlreadyExistsException(email);
		}
		if (!StringUtils.hasText(req.getPassword())) {
			throw new BadRequestException("Password is required for a new user");
		}
		User user = User.builder()
				.firstName(req.getFirstName().trim())
				.lastName(ServiceUtils.trimToNull(req.getLastName()))
				.email(email)
				.password(passwordEncoder.encode(req.getPassword()))
				.phone(ServiceUtils.trimToNull(req.getPhone()))
				.role(findRole(req.getRole()))
				.enabled(req.getEnabled() == null || req.getEnabled())
				.build();
		return userMapper.toResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public UserResponse updateUser(Long adminId, Long id, AdminUserRequest req) {
		User user = findUser(id);
		String email = req.getEmail().trim().toLowerCase();
		if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(email)) {
			throw new UserAlreadyExistsException(email);
		}
		boolean self = Objects.equals(adminId, id);
		if (self && req.getRole() != user.getRole().getName()) {
			throw new BadRequestException("You cannot change your own role");
		}
		if (self && Boolean.FALSE.equals(req.getEnabled())) {
			throw new BadRequestException("You cannot disable your own account");
		}
		user.setFirstName(req.getFirstName().trim());
		user.setLastName(ServiceUtils.trimToNull(req.getLastName()));
		user.setEmail(email);
		user.setPhone(ServiceUtils.trimToNull(req.getPhone()));
		user.setRole(findRole(req.getRole()));
		if (req.getEnabled() != null) {
			user.setEnabled(req.getEnabled());
		}
		if (StringUtils.hasText(req.getPassword())) {
			user.setPassword(passwordEncoder.encode(req.getPassword()));
		}
		return userMapper.toResponse(user);
	}

	@Override
	@Transactional
	public UserResponse setEnabled(Long adminId, Long id, boolean enabled) {
		if (Objects.equals(adminId, id) && !enabled) {
			throw new BadRequestException("You cannot disable your own account");
		}
		User user = findUser(id);
		user.setEnabled(enabled);
		return userMapper.toResponse(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long adminId, Long id) {
		if (Objects.equals(adminId, id)) {
			throw new BadRequestException("You cannot delete your own account");
		}
		User user = findUser(id);
		if (orderRepository.existsByUserId(id) || deliveryRepository.existsByDeliveryPartnerId(id)) {
			throw new BadRequestException("This user has order or delivery history. Disable the account instead.");
		}
		cartRepository.findByUserId(id).ifPresent(cartRepository::delete);
		wishlistRepository.findByUserId(id).ifPresent(wishlistRepository::delete);
		addressRepository.deleteByUserId(id);
		reviewRepository.deleteByUserId(id);
		customerRequestRepository.deleteByUserId(id);
		customerProfileRepository.deleteByUserId(id);
		staffProfileRepository.deleteByUserId(id);
		deliveryProfileRepository.deleteByUserId(id);
		emailOtpRepository.deleteByEmail(user.getEmail());
		userRepository.delete(user);
		userRepository.flush();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> listDeliveryPartners() {
		return userRepository.findByRole_NameAndEnabledTrueAndApprovalStatusOrderByFirstNameAsc(RoleName.DELIVERY_PARTNER, ApprovalStatus.APPROVED).stream()
				.map(userMapper::toResponse)
				.toList();
	}

	private User findUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}

	private Role findRole(RoleName name) {
		return roleRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
	}
}
