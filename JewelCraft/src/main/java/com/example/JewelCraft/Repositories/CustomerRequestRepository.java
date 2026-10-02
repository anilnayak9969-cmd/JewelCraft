package com.example.JewelCraft.Repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.JewelCraft.Entites.CustomerRequest;
import com.example.JewelCraft.Enum.RequestStatus;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {

	List<CustomerRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

	void deleteByUserId(Long userId);

	long countByStatusIn(List<RequestStatus> statuses);

	@Query("select r from CustomerRequest r where (:status is null or r.status = :status) order by r.createdAt desc")
	Page<CustomerRequest> search(@Param("status") RequestStatus status, Pageable pageable);
}
