package com.example.JewelCraft.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.EmailOtp;
import com.example.JewelCraft.Enum.OtpPurpose;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

	Optional<EmailOtp> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);

	List<EmailOtp> findByEmailAndPurposeAndConsumedFalse(String email, OtpPurpose purpose);

	long countByEmailAndPurposeAndCreatedAtAfter(String email, OtpPurpose purpose, LocalDateTime after);

	void deleteByEmail(String email);
}
