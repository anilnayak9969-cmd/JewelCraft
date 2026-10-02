package com.example.JewelCraft.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.MetalRate;
import com.example.JewelCraft.Enum.MetalType;

public interface MetalRateRepository extends JpaRepository<MetalRate, Long> {

	Optional<MetalRate> findByMetalTypeAndPurityIgnoreCase(MetalType metalType, String purity);

	List<MetalRate> findAllByOrderByMetalTypeAscPurityDesc();
}
