package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Dto.PricingRequest;
import com.example.JewelCraft.Dto.ProductFilter;
import com.example.JewelCraft.Dto.ProductRequest;
import com.example.JewelCraft.Dto.ProductResponse;
import com.example.JewelCraft.Entites.Category;
import com.example.JewelCraft.Entites.Inventory;
import com.example.JewelCraft.Entites.MetalRate;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Entites.ProductImage;
import com.example.JewelCraft.Enum.MetalType;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.ProductMapper;
import com.example.JewelCraft.Repositories.CategoryRepository;
import com.example.JewelCraft.Repositories.MetalRateRepository;
import com.example.JewelCraft.Repositories.ProductRepository;
import com.example.JewelCraft.Repositories.ReviewRepository;
import com.example.JewelCraft.Services.FileStorageService;
import com.example.JewelCraft.Services.PricingService;
import com.example.JewelCraft.Services.ProductService;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private static final int MAX_IMAGES = 8;

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final MetalRateRepository metalRateRepository;
	private final ReviewRepository reviewRepository;
	private final PricingService pricingService;
	private final FileStorageService fileStorageService;
	private final ProductMapper mapper;

	// ---------------------------------------------------------------- listing

	@Override
	@Transactional(readOnly = true)
	public PageResponse<ProductResponse> searchProducts(ProductFilter filter) {
		return search(filter, true);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<ProductResponse> searchAllProducts(ProductFilter filter) {
		return search(filter, false);
	}

	private PageResponse<ProductResponse> search(ProductFilter f, boolean shopView) {
		Specification<Product> spec = buildSpecification(f, shopView);
		var pageable = ServiceUtils.page(f.getPage(), f.getSize(), sortFor(f.getSort()));
		return PageResponse.of(productRepository.findAll(spec, pageable), mapper::toResponse);
	}

	private Specification<Product> buildSpecification(ProductFilter f, boolean shopView) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (shopView) {
				predicates.add(cb.isTrue(root.get("active")));
				predicates.add(cb.isTrue(root.get("category").get("active")));
			} else if (f.getActive() != null) {
				predicates.add(cb.equal(root.get("active"), f.getActive()));
			}
			String pattern = ServiceUtils.likePattern(f.getSearch());
			if (pattern != null) {
				predicates.add(cb.or(
						cb.like(cb.lower(root.<String>get("name")), pattern),
						cb.like(cb.lower(root.<String>get("sku")), pattern),
						cb.like(cb.lower(root.get("category").<String>get("name")), pattern),
						cb.like(cb.lower(root.<String>get("purity")), pattern)));
			}
			if (f.getCategoryId() != null) {
				predicates.add(cb.equal(root.get("category").get("id"), f.getCategoryId()));
			}
			if (f.getMetalType() != null) {
				predicates.add(cb.equal(root.get("metalType"), f.getMetalType()));
			}
			if (StringUtils.hasText(f.getPurity())) {
				predicates.add(cb.equal(cb.lower(root.<String>get("purity")), f.getPurity().trim().toLowerCase()));
			}
			if (f.getMinPrice() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("finalPrice"), f.getMinPrice()));
			}
			if (f.getMaxPrice() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("finalPrice"), f.getMaxPrice()));
			}
			if (Boolean.TRUE.equals(f.getFeatured())) {
				predicates.add(cb.isTrue(root.get("featured")));
			}
			if (Boolean.TRUE.equals(f.getInStock())) {
				predicates.add(cb.greaterThan(root.join("inventory", JoinType.LEFT).<Integer>get("quantity"), 0));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Sort sortFor(String sort) {
		Sort byId = Sort.by(Sort.Direction.DESC, "id");
		if (sort == null) {
			return Sort.by(Sort.Direction.DESC, "createdAt").and(byId);
		}
		return switch (sort) {
			case "price_asc" -> Sort.by(Sort.Direction.ASC, "finalPrice").and(byId);
			case "price_desc" -> Sort.by(Sort.Direction.DESC, "finalPrice").and(byId);
			case "name_asc" -> Sort.by(Sort.Direction.ASC, "name").and(byId);
			case "weight_desc" -> Sort.by(Sort.Direction.DESC, "weight").and(byId);
			default -> Sort.by(Sort.Direction.DESC, "createdAt").and(byId);
		};
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getProduct(Long id, boolean publicView) {
		Product product = find(id);
		if (publicView && (!product.isActive() || !product.getCategory().isActive())) {
			throw new ResourceNotFoundException("Product", "id", id);
		}
		ProductResponse response = mapper.toDetailResponse(product);
		List<Object[]> summary = reviewRepository.ratingSummary(id);
		if (!summary.isEmpty()) {
			Object[] row = summary.get(0);
			double avg = ((Number) row[0]).doubleValue();
			response.setAverageRating(Math.round(avg * 10) / 10.0);
			response.setReviewCount(((Number) row[1]).longValue());
		}
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getFeaturedProducts() {
		return productRepository.findTop8ByActiveTrueAndFeaturedTrueOrderByCreatedAtDesc().stream()
				.map(mapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getLatestProducts() {
		return productRepository.findTop8ByActiveTrueOrderByCreatedAtDesc().stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getPurities() {
		return productRepository.findDistinctActivePurities();
	}

	// ---------------------------------------------------------------- create / update

	@Override
	@Transactional
	public ProductResponse createProduct(ProductRequest req) {
		String sku = StringUtils.hasText(req.getSku()) ? req.getSku().trim().toUpperCase() : generateSku(req.getMetalType());
		if (productRepository.existsBySkuIgnoreCase(sku)) {
			throw new BadRequestException("SKU " + sku + " is already used by another product");
		}
		Product product = new Product();
		product.setSku(sku);
		copyDetails(req, product);
		product.setActive(req.getActive() == null || req.getActive());

		Inventory inventory = Inventory.builder()
				.product(product)
				.quantity(req.getStockQuantity() == null ? 0 : req.getStockQuantity())
				.lowStockThreshold(req.getLowStockThreshold() == null ? 5 : req.getLowStockThreshold())
				.build();
		product.setInventory(inventory);

		return mapper.toDetailResponse(productRepository.save(product));
	}

	@Override
	@Transactional
	public ProductResponse updateProduct(Long id, ProductRequest req) {
		Product product = find(id);
		if (StringUtils.hasText(req.getSku())) {
			String sku = req.getSku().trim().toUpperCase();
			if (productRepository.existsBySkuIgnoreCaseAndIdNot(sku, id)) {
				throw new BadRequestException("SKU " + sku + " is already used by another product");
			}
			product.setSku(sku);
		}
		copyDetails(req, product);
		if (req.getActive() != null) {
			product.setActive(req.getActive());
		}
		Inventory inv = product.getInventory();
		if (inv != null) {
			if (req.getStockQuantity() != null) {
				inv.setQuantity(req.getStockQuantity());
			}
			if (req.getLowStockThreshold() != null) {
				inv.setLowStockThreshold(req.getLowStockThreshold());
			}
		}
		return mapper.toDetailResponse(product);
	}

	/** Copies the editable fields and recalculates the price from the current metal rate. */
	private void copyDetails(ProductRequest req, Product p) {
		Category category = categoryRepository.findById(req.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category", "id", req.getCategoryId()));
		MetalRate rate = findRate(req.getMetalType(), req.getPurity());

		p.setName(req.getName().trim());
		p.setDescription(ServiceUtils.trimToNull(req.getDescription()));
		p.setCategory(category);
		p.setMetalType(rate.getMetalType());
		p.setPurity(rate.getPurity());
		p.setWeight(req.getWeight());
		p.setMakingCharges(req.getMakingCharges());
		p.setStoneCharges(orZero(req.getStoneCharges()));
		p.setDiscount(orZero(req.getDiscount()));
		p.setTax(req.getTax() == null ? new BigDecimal("3.00") : req.getTax());
		p.setFeatured(Boolean.TRUE.equals(req.getFeatured()));
		pricingService.applyPrice(p, rate.getPricePerGram());
	}

	@Override
	@Transactional
	public ProductResponse updatePricing(Long id, PricingRequest req) {
		Product p = find(id);
		p.setWeight(req.getWeight());
		p.setMakingCharges(req.getMakingCharges());
		p.setStoneCharges(req.getStoneCharges());
		p.setDiscount(req.getDiscount());
		p.setTax(req.getTax());
		// always use today's rate, not the one stored when the product was last priced
		pricingService.applyPrice(p, findRate(p.getMetalType(), p.getPurity()).getPricePerGram());
		return mapper.toDetailResponse(p);
	}

	@Override
	@Transactional
	public ProductResponse setAvailability(Long id, boolean active) {
		Product p = find(id);
		p.setActive(active);
		return mapper.toDetailResponse(p);
	}

	@Override
	@Transactional
	public void deleteProduct(Long id) {
		find(id).setActive(false);
	}

	// ---------------------------------------------------------------- images

	@Override
	@Transactional
	public ProductResponse addImages(Long productId, List<MultipartFile> files) {
		Product p = find(productId);
		if (files == null || files.isEmpty()) {
			throw new BadRequestException("Please choose at least one image");
		}
		ensureRoom(p, files.size());
		for (MultipartFile file : files) {
			addImage(p, fileStorageService.storeProductImage(file));
		}
		syncMainImage(p);
		return mapper.toDetailResponse(p);
	}

	@Override
	@Transactional
	public ProductResponse addImageUrl(Long productId, String imageUrl) {
		Product p = find(productId);
		ensureRoom(p, 1);
		addImage(p, imageUrl.trim());
		syncMainImage(p);
		return mapper.toDetailResponse(p);
	}

	@Override
	@Transactional
	public ProductResponse replaceImage(Long productId, Long imageId, MultipartFile file) {
		Product p = find(productId);
		ProductImage image = findImage(p, imageId);
		String oldUrl = image.getImageUrl();
		image.setImageUrl(fileStorageService.storeProductImage(file));
		fileStorageService.deleteIfLocal(oldUrl);
		syncMainImage(p);
		return mapper.toDetailResponse(p);
	}

	@Override
	@Transactional
	public ProductResponse setMainImage(Long productId, Long imageId) {
		Product p = find(productId);
		ProductImage image = findImage(p, imageId);
		p.getImages().forEach(i -> i.setMainImage(false));
		image.setMainImage(true);
		syncMainImage(p);
		return mapper.toDetailResponse(p);
	}

	@Override
	@Transactional
	public ProductResponse deleteImage(Long productId, Long imageId) {
		Product p = find(productId);
		ProductImage image = findImage(p, imageId);
		p.getImages().remove(image);
		fileStorageService.deleteIfLocal(image.getImageUrl());
		syncMainImage(p);
		return mapper.toDetailResponse(p);
	}

	private void addImage(Product p, String url) {
		ProductImage image = ProductImage.builder().product(p).imageUrl(url).mainImage(false).build();
		p.getImages().add(image);
	}

	private void ensureRoom(Product p, int adding) {
		if (p.getImages().size() + adding > MAX_IMAGES) {
			throw new BadRequestException("A product can have at most " + MAX_IMAGES + " images");
		}
	}

	/** Exactly one image is "main" (the first one if none chosen), and product.imageUrl mirrors it. */
	private void syncMainImage(Product p) {
		List<ProductImage> images = p.getImages();
		if (images.isEmpty()) {
			p.setImageUrl(null);
			return;
		}
		ProductImage main = images.stream().filter(ProductImage::isMainImage).findFirst().orElse(images.get(0));
		images.forEach(i -> i.setMainImage(i == main));
		p.setImageUrl(main.getImageUrl());
	}

	private ProductImage findImage(Product p, Long imageId) {
		return p.getImages().stream().filter(i -> i.getId().equals(imageId)).findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
	}

	// ---------------------------------------------------------------- helpers

	private Product find(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
	}

	private MetalRate findRate(MetalType metal, String purity) {
		return metalRateRepository.findByMetalTypeAndPurityIgnoreCase(metal, purity.trim())
				.orElseThrow(() -> new BadRequestException("No metal rate is set for " + metal + " " + purity
						+ ". Add it in Price Management first."));
	}

	private String generateSku(MetalType metal) {
		String sku;
		do {
			sku = "JC-" + metal.name().charAt(0)
					+ UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
		} while (productRepository.existsBySkuIgnoreCase(sku));
		return sku;
	}

	private static BigDecimal orZero(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}
}
