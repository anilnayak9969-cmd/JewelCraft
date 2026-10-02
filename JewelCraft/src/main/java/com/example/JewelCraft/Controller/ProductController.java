package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.JewelCraft.Dto.ActiveRequest;
import com.example.JewelCraft.Dto.ImageUrlRequest;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PricingRequest;
import com.example.JewelCraft.Dto.ProductFilter;
import com.example.JewelCraft.Dto.ProductRequest;
import com.example.JewelCraft.Dto.ProductResponse;
import com.example.JewelCraft.Dto.ReviewRequest;
import com.example.JewelCraft.Dto.ReviewResponse;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.ProductService;
import com.example.JewelCraft.Services.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private static final String STORE_TEAM = "hasAnyRole('STAFF', 'STORE_MANAGER', 'ADMIN')";
	private static final String MANAGERS = "hasAnyRole('STORE_MANAGER', 'ADMIN')";

	private final ProductService productService;
	private final ReviewService reviewService;

	// ------------------------------------------------ shop (public)

	/** e.g. /api/products?search=ring&categoryId=1&metalType=GOLD&minPrice=10000&sort=price_asc&page=0&size=12 */
	@GetMapping
	public PageResponse<ProductResponse> searchProducts(@ModelAttribute ProductFilter filter) {
		return productService.searchProducts(filter);
	}

	@GetMapping("/{id}")
	public ProductResponse getProduct(@PathVariable Long id) {
		return productService.getProduct(id, true);
	}

	@GetMapping("/featured")
	public List<ProductResponse> getFeatured() {
		return productService.getFeaturedProducts();
	}

	@GetMapping("/latest")
	public List<ProductResponse> getLatest() {
		return productService.getLatestProducts();
	}

	@GetMapping("/purities")
	public List<String> getPurities() {
		return productService.getPurities();
	}

	@GetMapping("/{productId}/reviews")
	public PageResponse<ReviewResponse> getReviews(@PathVariable Long productId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		return reviewService.getProductReviews(productId, page, size);
	}

	@PostMapping("/{productId}/reviews")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('CUSTOMER')")
	public ReviewResponse addReview(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long productId,
			@Valid @RequestBody ReviewRequest request) {
		return reviewService.addReview(user.getId(), productId, request);
	}

	// ------------------------------------------------ management

	/** Same filters as the shop listing plus ?active=true|false; includes inactive products. */
	@GetMapping("/manage")
	public PageResponse<ProductResponse> searchAllProducts(@ModelAttribute ProductFilter filter) {
		return productService.searchAllProducts(filter);
	}

	@GetMapping("/manage/{id}")
	public ProductResponse getProductForManagement(@PathVariable Long id) {
		return productService.getProduct(id, false);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize(STORE_TEAM)
	public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
		return productService.createProduct(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize(STORE_TEAM)
	public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
		return productService.updateProduct(id, request);
	}

	@PutMapping("/{id}/pricing")
	@PreAuthorize(MANAGERS)
	public ProductResponse updatePricing(@PathVariable Long id, @Valid @RequestBody PricingRequest request) {
		return productService.updatePricing(id, request);
	}

	@PatchMapping("/{id}/availability")
	@PreAuthorize(MANAGERS)
	public ProductResponse setAvailability(@PathVariable Long id, @Valid @RequestBody ActiveRequest request) {
		return productService.setAvailability(id, request.active());
	}

	/** Soft delete (deactivate). */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize(MANAGERS)
	public void deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
	}

	// ------------------------------------------------ images

	@PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize(STORE_TEAM)
	public ProductResponse uploadImages(@PathVariable Long id, @RequestPart("files") List<MultipartFile> files) {
		return productService.addImages(id, files);
	}

	@PostMapping("/{id}/images/url")
	@PreAuthorize(STORE_TEAM)
	public ProductResponse addImageUrl(@PathVariable Long id, @Valid @RequestBody ImageUrlRequest request) {
		return productService.addImageUrl(id, request.getImageUrl());
	}

	@PutMapping(value = "/{id}/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize(STORE_TEAM)
	public ProductResponse replaceImage(@PathVariable Long id, @PathVariable Long imageId,
			@RequestPart("file") MultipartFile file) {
		return productService.replaceImage(id, imageId, file);
	}

	@PutMapping("/{id}/images/{imageId}/main")
	@PreAuthorize(STORE_TEAM)
	public ProductResponse setMainImage(@PathVariable Long id, @PathVariable Long imageId) {
		return productService.setMainImage(id, imageId);
	}

	@DeleteMapping("/{id}/images/{imageId}")
	@PreAuthorize(MANAGERS)
	public ProductResponse deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
		return productService.deleteImage(id, imageId);
	}
}
