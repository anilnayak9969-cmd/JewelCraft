package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.InventoryResponse;
import com.example.JewelCraft.Dto.InventoryUpdateRequest;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Services.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** STAFF, STORE_MANAGER and ADMIN (enforced in WebSecurityConfig). */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

	private final InventoryService inventoryService;

	@GetMapping
	public PageResponse<InventoryResponse> getInventory(@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "false") boolean lowOnly, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return inventoryService.getInventory(search, lowOnly, page, size);
	}

	@GetMapping("/low-stock")
	public List<InventoryResponse> getLowStock() {
		return inventoryService.getLowStock();
	}

	@PutMapping("/{productId}")
	public InventoryResponse updateStock(@PathVariable Long productId,
			@Valid @RequestBody InventoryUpdateRequest request) {
		return inventoryService.updateStock(productId, request);
	}
}
