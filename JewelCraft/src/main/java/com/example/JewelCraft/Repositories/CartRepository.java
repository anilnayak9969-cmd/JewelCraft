package com.example.JewelCraft.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByUserId(Long userId);
}
