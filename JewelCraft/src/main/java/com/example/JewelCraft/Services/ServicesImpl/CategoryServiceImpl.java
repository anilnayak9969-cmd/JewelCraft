package com.example.JewelCraft.Services.ServicesImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.CategoryRequest;
import com.example.JewelCraft.Dto.CategoryResponse;
import com.example.JewelCraft.Entites.Category;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.ProductMapper;
import com.example.JewelCraft.Repositories.CategoryRepository;
import com.example.JewelCraft.Repositories.ProductRepository;
import com.example.JewelCraft.Services.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final ProductMapper mapper;

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getCategories(boolean activeOnly) {
		Map<Long, Long> counts = new HashMap<>();
		for (Object[] row : productRepository.countGroupedByCategoryId()) {
			counts.put((Long) row[0], (Long) row[1]);
		}
		List<Category> categories = activeOnly ? categoryRepository.findByActiveTrueOrderByNameAsc()
				: categoryRepository.findAllByOrderByNameAsc();
		return categories.stream().map(c -> mapper.toResponse(c, counts.getOrDefault(c.getId(), 0L))).toList();
	}

	@Override
	@Transactional
	public CategoryResponse createCategory(CategoryRequest req) {
		String name = req.getName().trim();
		if (categoryRepository.existsByNameIgnoreCase(name)) {
			throw new BadRequestException("Category '" + name + "' already exists");
		}
		Category category = Category.builder()
				.name(name)
				.description(ServiceUtils.trimToNull(req.getDescription()))
				.imageUrl(ServiceUtils.trimToNull(req.getImageUrl()))
				.active(req.getActive() == null || req.getActive())
				.build();
		return mapper.toResponse(categoryRepository.save(category), 0);
	}

	@Override
	@Transactional
	public CategoryResponse updateCategory(Long id, CategoryRequest req) {
		Category category = find(id);
		String name = req.getName().trim();
		if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
			throw new BadRequestException("Category '" + name + "' already exists");
		}
		category.setName(name);
		category.setDescription(ServiceUtils.trimToNull(req.getDescription()));
		category.setImageUrl(ServiceUtils.trimToNull(req.getImageUrl()));
		if (req.getActive() != null) {
			category.setActive(req.getActive());
		}
		return mapper.toResponse(category, 0);
	}

	@Override
	@Transactional
	public void deleteCategory(Long id) {
		Category category = find(id);
		if (productRepository.existsByCategoryId(id)) {
			throw new BadRequestException("This category still has products. Move them or deactivate the category instead.");
		}
		categoryRepository.delete(category);
	}

	private Category find(Long id) {
		return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
	}
}
