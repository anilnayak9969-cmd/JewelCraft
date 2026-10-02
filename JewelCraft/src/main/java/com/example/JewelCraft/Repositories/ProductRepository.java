package com.example.JewelCraft.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Enum.MetalType;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	List<Product> findByMetalTypeAndPurityIgnoreCase(MetalType metalType, String purity);

	boolean existsByMetalTypeAndPurityIgnoreCase(MetalType metalType, String purity);

	boolean existsByCategoryId(Long categoryId);

	boolean existsBySkuIgnoreCase(String sku);

	boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

	long countByActiveTrue();

	List<Product> findTop8ByActiveTrueAndFeaturedTrueOrderByCreatedAtDesc();

	List<Product> findTop8ByActiveTrueOrderByCreatedAtDesc();

	@Query("select distinct p.purity from Product p where p.active = true order by p.purity")
	List<String> findDistinctActivePurities();

	@Query("select p.category.id, count(p) from Product p group by p.category.id")
	List<Object[]> countGroupedByCategoryId();

	@Query("select p.category.name, count(p) from Product p where p.active = true group by p.category.name order by count(p) desc")
	List<Object[]> countActiveGroupedByCategoryName();
}
