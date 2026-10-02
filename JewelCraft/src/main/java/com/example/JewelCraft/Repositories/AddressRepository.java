package com.example.JewelCraft.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByUserIdOrderByDefaultAddressDescIdDesc(Long userId);

	Optional<Address> findByIdAndUserId(Long id, Long userId);

	long countByUserId(Long userId);

	void deleteByUserId(Long userId);
}
