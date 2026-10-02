package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.CategoryRequest;
import com.example.JewelCraft.Dto.CategoryResponse;

public interface CategoryService {

	/** activeOnly = true for the shop, false for management screens. */
	List<CategoryResponse> getCategories(boolean activeOnly);

	CategoryResponse createCategory(CategoryRequest request);

	CategoryResponse updateCategory(Long id, CategoryRequest request);

	void deleteCategory(Long id);
}
