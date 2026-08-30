package com.stockflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockflow.dto.request.CategoryRequest;
import com.stockflow.dto.response.CategoryResponse;
import com.stockflow.entity.Category;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.CategoryMapper;
import com.stockflow.repository.CategoryRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTests {

	@Mock
	private CategoryRepository categoryRepository;

	private CategoryServiceImpl categoryService;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryServiceImpl(categoryRepository, new CategoryMapper());
	}

	@Test
	void shouldCreateCategory() {
		CategoryRequest request = new CategoryRequest("Food", "Food products");
		Category savedCategory = buildCategory(1L, "Food", "Food products");

		when(categoryRepository.existsByNameIgnoreCase("Food")).thenReturn(false);
		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

		CategoryResponse response = categoryService.createCategory(request);

		assertNotNull(response);
		assertEquals(1L, response.id());
		assertEquals("Food", response.name());
		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	void shouldRejectDuplicateCategoryNameOnCreate() {
		CategoryRequest request = new CategoryRequest("Food", "Food products");

		when(categoryRepository.existsByNameIgnoreCase("Food")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(request));
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void shouldReturnAllCategories() {
		Category first = buildCategory(1L, "Food", "Food products");
		Category second = buildCategory(2L, "Beverages", "Drink products");

		when(categoryRepository.findAll()).thenReturn(List.of(first, second));

		List<CategoryResponse> responses = categoryService.getAllCategories();

		assertEquals(2, responses.size());
		assertEquals("Food", responses.getFirst().name());
		assertEquals("Beverages", responses.get(1).name());
	}

	@Test
	void shouldReturnCategoryById() {
		Category category = buildCategory(1L, "Food", "Food products");

		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

		CategoryResponse response = categoryService.getCategoryById(1L);

		assertEquals(1L, response.id());
		assertEquals("Food", response.name());
	}

	@Test
	void shouldThrowWhenCategoryNotFound() {
		when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
	}

	@Test
	void shouldUpdateCategory() {
		Category existingCategory = buildCategory(1L, "Food", "Food products");
		Category updatedCategory = buildCategory(1L, "Fresh Food", "Fresh food products");
		CategoryRequest request = new CategoryRequest("Fresh Food", "Fresh food products");

		when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
		when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Fresh Food", 1L)).thenReturn(false);
		when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);

		CategoryResponse response = categoryService.updateCategory(1L, request);

		assertEquals("Fresh Food", response.name());
		assertEquals("Fresh food products", response.description());
	}

	@Test
	void shouldDeleteCategory() {
		Category category = buildCategory(1L, "Food", "Food products");

		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

		categoryService.deleteCategory(1L);

		verify(categoryRepository).delete(category);
	}

	private Category buildCategory(Long id, String name, String description) {
		Category category = new Category();
		category.setId(id);
		category.setName(name);
		category.setDescription(description);
		category.setCreatedAt(Instant.parse("2026-08-30T10:15:30Z"));
		category.setUpdatedAt(Instant.parse("2026-08-30T10:15:30Z"));
		return category;
	}
}
