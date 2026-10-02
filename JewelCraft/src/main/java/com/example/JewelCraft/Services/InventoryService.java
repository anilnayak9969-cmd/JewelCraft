package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.InventoryResponse;
import com.example.JewelCraft.Dto.InventoryUpdateRequest;
import com.example.JewelCraft.Dto.PageResponse;

public interface InventoryService {

	PageResponse<InventoryResponse> getInventory(String search, boolean lowOnly, int page, int size);

	List<InventoryResponse> getLowStock();

	InventoryResponse updateStock(Long productId, InventoryUpdateRequest request);
}
