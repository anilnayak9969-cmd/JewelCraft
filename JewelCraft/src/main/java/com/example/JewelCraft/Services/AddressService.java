package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.AddressRequest;
import com.example.JewelCraft.Dto.AddressResponse;

public interface AddressService {

	List<AddressResponse> getAddresses(Long userId);

	AddressResponse addAddress(Long userId, AddressRequest request);

	AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

	AddressResponse setDefault(Long userId, Long addressId);

	void deleteAddress(Long userId, Long addressId);
}
