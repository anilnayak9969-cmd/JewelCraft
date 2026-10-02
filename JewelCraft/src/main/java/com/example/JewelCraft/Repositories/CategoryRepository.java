package com.example.JewelCraft.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JewelCraft.Entites.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	boolean existsByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

	List<Category> findAllByOrderByNameAsc();

	List<Category> findByActiveTrueOrderByNameAsc();
}
