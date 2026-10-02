package com.example.JewelCraft.Services.ServicesImpl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.InventoryResponse;
import com.example.JewelCraft.Dto.InventoryUpdateRequest;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Entites.Inventory;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.ProductMapper;
import com.example.JewelCraft.Repositories.InventoryRepository;
import com.example.JewelCraft.Services.InventoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepository inventoryRepository;
	private final ProductMapper mapper;

	@Override
	@Transactional(readOnly = true)
	public PageResponse<InventoryResponse> getInventory(String search, boolean lowOnly, int page, int size) {
		var pageable = ServiceUtils.page(page, size, Sort.by(Sort.Direction.ASC, "quantity"));
		return PageResponse.of(inventoryRepository.search(ServiceUtils.likePattern(search), lowOnly, pageable),
				mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryResponse> getLowStock() {
		return inventoryRepository.findLowStock().stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional
	public InventoryResponse updateStock(Long productId, InventoryUpdateRequest req) {
		Inventory inv = inventoryRepository.findByProductId(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory for product", "id", productId));
		inv.setQuantity(req.getQuantity());
		if (req.getLowStockThreshold() != null) {
			inv.setLowStockThreshold(req.getLowStockThreshold());
		}
		return mapper.toResponse(inv);
	}
}
