package com.example.JewelCraft.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.StaffProfile;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {

	Optional<StaffProfile> findByUserId(Long userId);

	void deleteByUserId(Long userId);

	boolean existsByEmployeeCode(String employeeCode);
}
