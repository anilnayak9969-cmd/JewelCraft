package com.example.JewelCraft.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.JewelCraft.Entites.OrderTrackingEvent;

public interface OrderTrackingEventRepository extends JpaRepository<OrderTrackingEvent, Long> {

	List<OrderTrackingEvent> findByOrderIdOrderByCreatedAtAscIdAsc(Long orderId);

	@Query("select distinct e.order.id from OrderTrackingEvent e")
	List<Long> findOrderIdsWithEvents();
}
