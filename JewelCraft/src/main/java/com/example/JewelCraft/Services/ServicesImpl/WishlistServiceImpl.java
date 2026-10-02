package com.example.JewelCraft.Services.ServicesImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.WishlistItemResponse;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Entites.Wishlist;
import com.example.JewelCraft.Entites.WishlistItem;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.ProductMapper;
import com.example.JewelCraft.Repositories.ProductRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Repositories.WishlistRepository;
import com.example.JewelCraft.Services.WishlistService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

	private final WishlistRepository wishlistRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ProductMapper productMapper;

	@Override
	@Transactional
	public List<WishlistItemResponse> getWishlist(Long userId) {
		return toResponse(getOrCreate(userId));
	}

	@Override
	@Transactional
	public List<WishlistItemResponse> addProduct(Long userId, Long productId) {
		Wishlist wishlist = getOrCreate(userId);
		boolean alreadyThere = wishlist.getItems().stream().anyMatch(i -> i.getProduct().getId().equals(productId));
		if (!alreadyThere) {
			Product product = productRepository.findById(productId)
					.filter(Product::isActive)
					.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
			wishlist.getItems().add(0, WishlistItem.builder().wishlist(wishlist).product(product).build());
		}
		return toResponse(wishlist);
	}

	@Override
	@Transactional
	public List<WishlistItemResponse> removeProduct(Long userId, Long productId) {
		Wishlist wishlist = getOrCreate(userId);
		wishlist.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
		return toResponse(wishlist);
	}

	private Wishlist getOrCreate(Long userId) {
		return wishlistRepository.findByUserId(userId).orElseGet(() -> wishlistRepository
				.save(Wishlist.builder().user(userRepository.getReferenceById(userId)).build()));
	}

	private List<WishlistItemResponse> toResponse(Wishlist wishlist) {
		return wishlist.getItems().stream()
				.map(i -> new WishlistItemResponse(i.getId(), productMapper.toResponse(i.getProduct()), i.getAddedAt()))
				.toList();
	}
}
