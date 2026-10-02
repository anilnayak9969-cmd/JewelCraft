package com.example.JewelCraft.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.JewelCraft.Entites.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	/** [productName, quantitySold, revenue] - best sellers first. */
	@Query("""
			select oi.productName, sum(oi.quantity), sum(oi.lineTotal)
			from OrderItem oi
			where oi.order.status <> com.example.JewelCraft.Enum.OrderStatus.CANCELLED
			  and oi.order.createdAt between :from and :to
			group by oi.productName
			order by sum(oi.quantity) desc
			""")
	List<Object[]> topProducts(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

	/** [categoryName, revenue] */
	@Query("""
			select p.category.name, sum(oi.lineTotal)
			from OrderItem oi join oi.product p
			where oi.order.status <> com.example.JewelCraft.Enum.OrderStatus.CANCELLED
			  and oi.order.createdAt between :from and :to
			group by p.category.name
			order by sum(oi.lineTotal) desc
			""")
	List<Object[]> salesByCategory(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
