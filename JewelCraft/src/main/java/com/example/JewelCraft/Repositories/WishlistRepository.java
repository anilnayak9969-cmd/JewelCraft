package com.example.JewelCraft.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

	Optional<Wishlist> findByUserId(Long userId);
}
