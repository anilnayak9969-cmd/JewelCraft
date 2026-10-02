package com.example.JewelCraft.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.DeliveryPartnerProfile;

public interface DeliveryPartnerProfileRepository extends JpaRepository<DeliveryPartnerProfile, Long> {

	Optional<DeliveryPartnerProfile> findByUserId(Long userId);

	void deleteByUserId(Long userId);
}
