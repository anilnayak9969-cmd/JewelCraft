package com.example.JewelCraft.Services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PricingRequest;
import com.example.JewelCraft.Dto.ProductFilter;
import com.example.JewelCraft.Dto.ProductRequest;
import com.example.JewelCraft.Dto.ProductResponse;

public interface ProductService {

	/** Shop listing: only active products in active categories. */
	PageResponse<ProductResponse> searchProducts(ProductFilter filter);

	/** Management listing: includes inactive products. */
	PageResponse<ProductResponse> searchAllProducts(ProductFilter filter);

	/** publicView = true hides inactive products (404). */
	ProductResponse getProduct(Long id, boolean publicView);

	List<ProductResponse> getFeaturedProducts();

	List<ProductResponse> getLatestProducts();

	List<String> getPurities();

	ProductResponse createProduct(ProductRequest request);

	ProductResponse updateProduct(Long id, ProductRequest request);

	ProductResponse updatePricing(Long id, PricingRequest request);

	ProductResponse setAvailability(Long id, boolean active);

	/** Soft delete - products appear in past orders, so they are deactivated instead of removed. */
	void deleteProduct(Long id);

	// --- images ---
	ProductResponse addImages(Long productId, List<MultipartFile> files);

	ProductResponse addImageUrl(Long productId, String imageUrl);

	ProductResponse replaceImage(Long productId, Long imageId, MultipartFile file);

	ProductResponse setMainImage(Long productId, Long imageId);

	ProductResponse deleteImage(Long productId, Long imageId);
}
