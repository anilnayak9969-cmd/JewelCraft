package com.example.JewelCraft.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

	Optional<ProductImage> findByIdAndProductId(Long id, Long productId);
}
