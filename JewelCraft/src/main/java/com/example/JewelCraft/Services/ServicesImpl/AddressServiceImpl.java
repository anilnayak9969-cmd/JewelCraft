package com.example.JewelCraft.Services.ServicesImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.JewelCraft.Dto.AddressRequest;
import com.example.JewelCraft.Dto.AddressResponse;
import com.example.JewelCraft.Entites.Address;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.UserMapper;
import com.example.JewelCraft.Repositories.AddressRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Services.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

	private static final int MAX_ADDRESSES = 10;

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	@Transactional(readOnly = true)
	public List<AddressResponse> getAddresses(Long userId) {
		return addressRepository.findByUserIdOrderByDefaultAddressDescIdDesc(userId).stream()
				.map(userMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public AddressResponse addAddress(Long userId, AddressRequest req) {
		long count = addressRepository.countByUserId(userId);
		if (count >= MAX_ADDRESSES) {
			throw new BadRequestException("You can save at most " + MAX_ADDRESSES + " addresses");
		}
		Address address = new Address();
		address.setUser(userRepository.getReferenceById(userId));
		copy(req, address);
		// first address is always the default
		boolean makeDefault = count == 0 || req.isDefaultAddress();
		if (makeDefault) {
			clearDefault(userId);
		}
		address.setDefaultAddress(makeDefault);
		return userMapper.toResponse(addressRepository.save(address));
	}

	@Override
	@Transactional
	public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest req) {
		Address address = find(userId, addressId);
		copy(req, address);
		if (req.isDefaultAddress() && !address.isDefaultAddress()) {
			clearDefault(userId);
			address.setDefaultAddress(true);
		}
		return userMapper.toResponse(address);
	}

	@Override
	@Transactional
	public AddressResponse setDefault(Long userId, Long addressId) {
		Address address = find(userId, addressId);
		clearDefault(userId);
		address.setDefaultAddress(true);
		return userMapper.toResponse(address);
	}

	@Override
	@Transactional
	public void deleteAddress(Long userId, Long addressId) {
		Address address = find(userId, addressId);
		boolean wasDefault = address.isDefaultAddress();
		addressRepository.delete(address);
		addressRepository.flush();
		if (wasDefault) {
			addressRepository.findByUserIdOrderByDefaultAddressDescIdDesc(userId).stream().findFirst()
					.ifPresent(a -> a.setDefaultAddress(true));
		}
	}

	private Address find(Long userId, Long addressId) {
		// filtering by user id means a customer can never read or edit someone else's address
		return addressRepository.findByIdAndUserId(addressId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
	}

	private void clearDefault(Long userId) {
		addressRepository.findByUserIdOrderByDefaultAddressDescIdDesc(userId)
				.forEach(a -> a.setDefaultAddress(false));
	}

	private void copy(AddressRequest req, Address a) {
		a.setFullName(req.getFullName().trim());
		a.setPhone(req.getPhone());
		a.setLine1(req.getLine1().trim());
		a.setLine2(ServiceUtils.trimToNull(req.getLine2()));
		a.setCity(req.getCity().trim());
		a.setState(req.getState().trim());
		a.setPincode(req.getPincode());
		a.setCountry(StringUtils.hasText(req.getCountry()) ? req.getCountry().trim() : "India");
	}
}
