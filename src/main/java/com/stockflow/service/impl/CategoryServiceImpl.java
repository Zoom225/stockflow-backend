package com.stockflow.service.impl;

import com.stockflow.dto.request.CategoryRequest;
import com.stockflow.dto.response.CategoryResponse;
import com.stockflow.entity.Category;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.CategoryMapper;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	@Override
	@Transactional
	public CategoryResponse createCategory(CategoryRequest request) {
		validateUniqueName(request.name(), null);

		Category category = categoryMapper.toEntity(request);
		Category savedCategory = categoryRepository.save(category);
		return categoryMapper.toResponse(savedCategory);
	}

	@Override
	public List<CategoryResponse> getAllCategories() {
		return categoryRepository.findAll().stream()
				.map(categoryMapper::toResponse)
				.toList();
	}

	@Override
	public CategoryResponse getCategoryById(Long id) {
		return categoryMapper.toResponse(findCategoryById(id));
	}

	@Override
	@Transactional
	public CategoryResponse updateCategory(Long id, CategoryRequest request) {
		Category category = findCategoryById(id);
		validateUniqueName(request.name(), id);

		categoryMapper.updateEntity(category, request);
		Category updatedCategory = categoryRepository.save(category);
		return categoryMapper.toResponse(updatedCategory);
	}

	@Override
	@Transactional
	public void deleteCategory(Long id) {
		Category category = findCategoryById(id);
		categoryRepository.delete(category);
	}

	private Category findCategoryById(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
	}

	private void validateUniqueName(String name, Long categoryId) {
		String normalizedName = name == null ? null : name.trim();
		boolean exists = categoryId == null
				? categoryRepository.existsByNameIgnoreCase(normalizedName)
				: categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, categoryId);

		if (exists) {
			throw new DuplicateResourceException("Category name already exists: " + normalizedName);
		}
	}
}
