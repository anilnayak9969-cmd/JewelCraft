package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.AddressRequest;
import com.example.JewelCraft.Dto.AddressResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Customer delivery addresses (CUSTOMER role, enforced in WebSecurityConfig). */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService;

	@GetMapping
	public List<AddressResponse> getAddresses(@AuthenticationPrincipal UserDetailsImpl user) {
		return addressService.getAddresses(user.getId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AddressResponse addAddress(@AuthenticationPrincipal UserDetailsImpl user,
			@Valid @RequestBody AddressRequest request) {
		return addressService.addAddress(user.getId(), request);
	}

	@PutMapping("/{id}")
	public AddressResponse updateAddress(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
			@Valid @RequestBody AddressRequest request) {
		return addressService.updateAddress(user.getId(), id, request);
	}

	@PutMapping("/{id}/default")
	public AddressResponse setDefault(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
		return addressService.setDefault(user.getId(), id);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAddress(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
		addressService.deleteAddress(user.getId(), id);
	}
}
