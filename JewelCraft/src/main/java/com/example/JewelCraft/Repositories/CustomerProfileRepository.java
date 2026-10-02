package com.example.JewelCraft.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.CustomerProfile;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

	Optional<CustomerProfile> findByUserId(Long userId);

	void deleteByUserId(Long userId);
}
