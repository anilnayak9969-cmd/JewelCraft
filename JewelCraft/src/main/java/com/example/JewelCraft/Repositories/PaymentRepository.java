package com.example.JewelCraft.Repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.JewelCraft.Entites.Payment;
import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByOrderId(Long orderId);

	@Query("""
			select p from Payment p
			where (:status is null or p.status = :status)
			  and (:method is null or p.method = :method)
			""")
	Page<Payment> search(@Param("status") PaymentStatus status, @Param("method") PaymentMethod method,
			Pageable pageable);

	/** Money actually received. */
	@Query("select coalesce(sum(p.amount), 0) from Payment p where p.status = com.example.JewelCraft.Enum.PaymentStatus.SUCCESS and p.paidAt between :from and :to")
	BigDecimal sumReceived(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	@Query("select coalesce(sum(p.amount), 0) from Payment p where p.status = com.example.JewelCraft.Enum.PaymentStatus.SUCCESS")
	BigDecimal sumReceivedAllTime();

	/** [method, count, amount] for successful payments. */
	@Query("""
			select p.method, count(p), coalesce(sum(p.amount), 0) from Payment p
			where p.status = com.example.JewelCraft.Enum.PaymentStatus.SUCCESS and p.paidAt between :from and :to
			group by p.method
			""")
	List<Object[]> receivedByMethod(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
