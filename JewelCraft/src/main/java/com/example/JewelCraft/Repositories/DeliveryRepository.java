package com.example.JewelCraft.Repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.Delivery;
import com.example.JewelCraft.Enum.DeliveryStatus;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

	List<Delivery> findByDeliveryPartnerIdAndStatusInOrderByAssignedAtDesc(Long partnerId,
			Collection<DeliveryStatus> statuses);

	List<Delivery> findByStatusInOrderByAssignedAtDesc(Collection<DeliveryStatus> statuses);

	boolean existsByDeliveryPartnerId(Long partnerId);

	long countByDeliveryPartnerIdAndStatusIn(Long partnerId, Collection<DeliveryStatus> statuses);

	long countByDeliveryPartnerIdAndStatusAndDeliveredAtGreaterThanEqual(Long partnerId, DeliveryStatus status,
			LocalDateTime from);
}
