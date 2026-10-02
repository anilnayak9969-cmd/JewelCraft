package com.example.JewelCraft.Repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.JewelCraft.Entites.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {

	Optional<Offer> findByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

	List<Offer> findAllByOrderByEndDateDesc();

	@Query("select o from Offer o where o.active = true and :today between o.startDate and o.endDate order by o.discountPercent desc")
	List<Offer> findActive(@Param("today") LocalDate today);
}
