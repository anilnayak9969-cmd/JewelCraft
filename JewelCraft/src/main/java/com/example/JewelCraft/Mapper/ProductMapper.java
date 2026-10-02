package com.example.JewelCraft.Mapper;

import org.springframework.stereotype.Component;

import com.example.JewelCraft.Dto.CategoryResponse;
import com.example.JewelCraft.Dto.InventoryResponse;
import com.example.JewelCraft.Dto.MetalRateResponse;
import com.example.JewelCraft.Dto.PriceBreakdown;
import com.example.JewelCraft.Dto.ProductImageResponse;
import com.example.JewelCraft.Dto.ProductResponse;
import com.example.JewelCraft.Entites.Category;
import com.example.JewelCraft.Entites.Inventory;
import com.example.JewelCraft.Entites.MetalRate;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Services.PricingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMapper {

	private final PricingService pricingService;

	/** Card view: no image gallery (keeps listing queries light). */
	public ProductResponse toResponse(Product p) {
		PriceBreakdown price = pricingService.breakdown(p);
		Inventory inv = p.getInventory();
		int stock = inv == null ? 0 : inv.getQuantity();
		return ProductResponse.builder()
				.id(p.getId())
				.name(p.getName())
				.sku(p.getSku())
				.description(p.getDescription())
				.categoryId(p.getCategory().getId())
				.categoryName(p.getCategory().getName())
				.metalType(p.getMetalType())
				.purity(p.getPurity())
				.weight(p.getWeight())
				.metalPrice(p.getMetalPrice())
				.metalValue(price.metalValue())
				.makingCharges(p.getMakingCharges())
				.stoneCharges(p.getStoneCharges())
				.subtotal(price.subtotal())
				.discount(price.discount())
				.discountedAmount(price.discountedAmount())
				.tax(p.getTax())
				.taxAmount(price.taxAmount())
				.finalPrice(p.getFinalPrice())
				.stockQuantity(stock)
				.lowStockThreshold(inv == null ? 0 : inv.getLowStockThreshold())
				.inStock(stock > 0)
				.imageUrl(p.getImageUrl())
				.featured(p.isFeatured())
				.active(p.isActive())
				.createdAt(p.getCreatedAt())
				.updatedAt(p.getUpdatedAt())
				.build();
	}

	/** Details view: includes the image gallery. */
	public ProductResponse toDetailResponse(Product p) {
		ProductResponse r = toResponse(p);
		r.setImages(p.getImages().stream()
				.map(i -> new ProductImageResponse(i.getId(), i.getImageUrl(), i.isMainImage()))
				.toList());
		return r;
	}

	public CategoryResponse toResponse(Category c, long productCount) {
		return CategoryResponse.builder()
				.id(c.getId())
				.name(c.getName())
				.description(c.getDescription())
				.imageUrl(c.getImageUrl())
				.active(c.isActive())
				.productCount(productCount)
				.build();
	}

	public InventoryResponse toResponse(Inventory i) {
		Product p = i.getProduct();
		return InventoryResponse.builder()
				.productId(p.getId())
				.productName(p.getName())
				.sku(p.getSku())
				.categoryName(p.getCategory().getName())
				.imageUrl(p.getImageUrl())
				.active(p.isActive())
				.quantity(i.getQuantity())
				.lowStockThreshold(i.getLowStockThreshold())
				.lowStock(i.isLowStock())
				.updatedAt(i.getUpdatedAt())
				.build();
	}

	public MetalRateResponse toResponse(MetalRate m) {
		return MetalRateResponse.builder()
				.id(m.getId())
				.metalType(m.getMetalType())
				.purity(m.getPurity())
				.pricePerGram(m.getPricePerGram())
				.updatedAt(m.getUpdatedAt())
				.build();
	}
}
