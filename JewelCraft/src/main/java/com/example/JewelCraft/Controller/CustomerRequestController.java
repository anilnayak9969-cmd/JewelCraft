package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.CustomerRequestCreate;
import com.example.JewelCraft.Dto.CustomerRequestResponse;
import com.example.JewelCraft.Dto.CustomerRequestUpdate;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Enum.RequestStatus;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.CustomerRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Customer support tickets: returns, exchanges, customisation, queries. */
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class CustomerRequestController {

	private final CustomerRequestService requestService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('CUSTOMER')")
	public CustomerRequestResponse createRequest(@AuthenticationPrincipal UserDetailsImpl user,
			@Valid @RequestBody CustomerRequestCreate request) {
		return requestService.createRequest(user.getId(), request);
	}

	@GetMapping("/my")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<CustomerRequestResponse> getMyRequests(@AuthenticationPrincipal UserDetailsImpl user) {
		return requestService.getMyRequests(user.getId());
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('STAFF', 'STORE_MANAGER', 'ADMIN')")
	public PageResponse<CustomerRequestResponse> searchRequests(@RequestParam(required = false) RequestStatus status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
		return requestService.searchRequests(status, page, size);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('STAFF', 'STORE_MANAGER', 'ADMIN')")
	public CustomerRequestResponse updateRequest(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
			@Valid @RequestBody CustomerRequestUpdate update) {
		return requestService.updateRequest(user.getId(), id, update);
	}
}
