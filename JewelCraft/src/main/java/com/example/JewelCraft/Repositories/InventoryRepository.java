package com.example.JewelCraft.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.JewelCraft.Entites.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	Optional<Inventory> findByProductId(Long productId);

	@Query("""
			select i from Inventory i join i.product p
			where (:pattern is null or lower(p.name) like :pattern or lower(p.sku) like :pattern)
			  and (:lowOnly = false or i.quantity <= i.lowStockThreshold)
			""")
	Page<Inventory> search(@Param("pattern") String pattern, @Param("lowOnly") boolean lowOnly, Pageable pageable);

	@Query("select i from Inventory i join fetch i.product p where i.quantity <= i.lowStockThreshold order by i.quantity asc")
	List<Inventory> findLowStock();

	@Query("select count(i) from Inventory i where i.quantity <= i.lowStockThreshold and i.product.active = true")
	long countLowStock();

	@Query("select coalesce(sum(i.quantity), 0) from Inventory i where i.product.active = true")
	long totalAvailableStock();
}
