package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.CartItemRequest;
import com.example.JewelCraft.Dto.CartItemResponse;
import com.example.JewelCraft.Dto.CartResponse;
import com.example.JewelCraft.Dto.PriceBreakdown;
import com.example.JewelCraft.Entites.Cart;
import com.example.JewelCraft.Entites.CartItem;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.InsufficientStockException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Repositories.CartRepository;
import com.example.JewelCraft.Repositories.ProductRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Services.CartService;
import com.example.JewelCraft.Services.PricingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	static final int MAX_QTY_PER_ITEM = 10;

	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final PricingService pricingService;

	@Override
	@Transactional
	public CartResponse getCart(Long userId) {
		return toResponse(getOrCreate(userId));
	}

	@Override
	@Transactional
	public CartResponse addItem(Long userId, CartItemRequest req) {
		Cart cart = getOrCreate(userId);
		Product product = productRepository.findById(req.getProductId())
				.filter(Product::isActive)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", req.getProductId()));

		CartItem item = cart.getItems().stream()
				.filter(i -> i.getProduct().getId().equals(product.getId()))
				.findFirst()
				.orElse(null);
		int newQty = (item == null ? 0 : item.getQuantity()) + req.getQuantity();
		checkQuantity(product, newQty);

		if (item == null) {
			cart.getItems().add(CartItem.builder().cart(cart).product(product).quantity(newQty).build());
			cartRepository.flush(); // assigns the new item's id, which the client needs to update/remove it
		} else {
			item.setQuantity(newQty);
		}
		return toResponse(cart);
	}

	@Override
	@Transactional
	public CartResponse updateItem(Long userId, Long itemId, int quantity) {
		Cart cart = getOrCreate(userId);
		CartItem item = findItem(cart, itemId);
		checkQuantity(item.getProduct(), quantity);
		item.setQuantity(quantity);
		return toResponse(cart);
	}

	@Override
	@Transactional
	public CartResponse removeItem(Long userId, Long itemId) {
		Cart cart = getOrCreate(userId);
		cart.getItems().remove(findItem(cart, itemId));
		return toResponse(cart);
	}

	@Override
	@Transactional
	public CartResponse clearCart(Long userId) {
		Cart cart = getOrCreate(userId);
		cart.getItems().clear();
		return toResponse(cart);
	}

	private Cart getOrCreate(Long userId) {
		return cartRepository.findByUserId(userId).orElseGet(
				() -> cartRepository.save(Cart.builder().user(userRepository.getReferenceById(userId)).build()));
	}

	private CartItem findItem(Cart cart, Long itemId) {
		return cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", itemId));
	}

	private void checkQuantity(Product product, int qty) {
		if (qty > MAX_QTY_PER_ITEM) {
			throw new BadRequestException("You can order at most " + MAX_QTY_PER_ITEM + " of " + product.getName());
		}
		if (qty > product.getStockQuantity()) {
			throw new InsufficientStockException(product.getName(), product.getStockQuantity());
		}
	}

	private CartResponse toResponse(Cart cart) {
		List<CartItemResponse> items = new ArrayList<>();
		BigDecimal subtotal = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		int count = 0;
		for (CartItem item : cart.getItems()) {
			Product p = item.getProduct();
			PriceBreakdown price = pricingService.breakdown(p);
			BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
			boolean available = p.isActive() && p.getStockQuantity() >= item.getQuantity();
			items.add(CartItemResponse.builder()
					.id(item.getId())
					.productId(p.getId())
					.productName(p.getName())
					.imageUrl(p.getImageUrl())
					.metalDetails(p.getPurity() + " " + p.getMetalType().name().replace('_', ' ') + " · " + p.getWeight() + " g")
					.unitPrice(price.discountedAmount())
					.finalPrice(price.finalPrice())
					.quantity(item.getQuantity())
					.lineTotal(price.finalPrice().multiply(qty))
					.stockQuantity(p.getStockQuantity())
					.available(available)
					.build());
			subtotal = subtotal.add(price.discountedAmount().multiply(qty));
			tax = tax.add(price.taxAmount().multiply(qty));
			count += item.getQuantity();
		}
		return CartResponse.builder()
				.items(items)
				.totalItems(count)
				.subtotal(subtotal)
				.tax(tax)
				.total(subtotal.add(tax))
				.build();
	}
}
