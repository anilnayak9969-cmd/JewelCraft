package com.example.JewelCraft.Repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.JewelCraft.Entites.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

	List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

	boolean existsByProductIdAndUserId(Long productId, Long userId);

	void deleteByUserId(Long userId);

	List<Review> findTop6ByRatingGreaterThanEqualOrderByCreatedAtDesc(int minRating);

	/** Single row: [average rating, review count]. */
	@Query("select coalesce(avg(r.rating), 0), count(r) from Review r where r.product.id = :productId")
	List<Object[]> ratingSummary(@Param("productId") Long productId);
}
