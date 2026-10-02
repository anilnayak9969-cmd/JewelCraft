package com.example.JewelCraft.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.JewelCraft.Dto.CategoryRequest;
import com.example.JewelCraft.Dto.CategoryResponse;
import com.example.JewelCraft.Services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	/** Public. includeInactive=true is used by the management screens. */
	@GetMapping
	public List<CategoryResponse> getCategories(@RequestParam(defaultValue = "false") boolean includeInactive) {
		return categoryService.getCategories(!includeInactive);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('STORE_MANAGER', 'ADMIN')")
	public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
		return categoryService.createCategory(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('STORE_MANAGER', 'ADMIN')")
	public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
		return categoryService.updateCategory(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('STORE_MANAGER', 'ADMIN')")
	public void deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
	}
}
