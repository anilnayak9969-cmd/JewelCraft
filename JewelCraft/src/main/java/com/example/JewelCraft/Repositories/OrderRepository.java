package com.example.JewelCraft.Repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Enum.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

	boolean existsByUserId(Long userId);

	boolean existsByOrderNumber(String orderNumber);

	long countByStatus(OrderStatus status);

	long countByStatusIn(List<OrderStatus> statuses);

	long countByCreatedAtGreaterThanEqual(LocalDateTime from);

	List<Order> findTop5ByOrderByCreatedAtDesc();

	@Query("""
			select o from Order o join o.user u
			where (:status is null or o.status = :status)
			  and (:pattern is null
			       or lower(o.orderNumber) like :pattern
			       or lower(u.email) like :pattern
			       or lower(u.firstName) like :pattern
			       or lower(o.shippingAddress.shipFullName) like :pattern)
			""")
	Page<Order> search(@Param("status") OrderStatus status, @Param("pattern") String pattern, Pageable pageable);

	@Query("select o.status, count(o) from Order o where o.createdAt between :from and :to group by o.status")
	List<Object[]> countGroupedByStatus(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	/** Value of all orders that were not cancelled. */
	@Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.status <> com.example.JewelCraft.Enum.OrderStatus.CANCELLED and o.createdAt between :from and :to")
	BigDecimal sumSales(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	@Query("select count(o) from Order o where o.createdAt between :from and :to")
	long countBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	@Query("""
			select year(o.createdAt), month(o.createdAt), count(o), coalesce(sum(o.totalAmount), 0)
			from Order o
			where o.status <> com.example.JewelCraft.Enum.OrderStatus.CANCELLED and o.createdAt between :from and :to
			group by year(o.createdAt), month(o.createdAt)
			order by year(o.createdAt), month(o.createdAt)
			""")
	List<Object[]> monthlySales(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
