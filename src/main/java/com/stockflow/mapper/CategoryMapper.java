package com.stockflow.mapper;

import com.stockflow.dto.request.CategoryRequest;
import com.stockflow.dto.response.CategoryResponse;
import com.stockflow.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

	public Category toEntity(CategoryRequest request) {
		Category category = new Category();
		category.setName(normalize(request.name()));
		category.setDescription(normalizeDescription(request.description()));
		return category;
	}

	public void updateEntity(Category category, CategoryRequest request) {
		category.setName(normalize(request.name()));
		category.setDescription(normalizeDescription(request.description()));
	}

	public CategoryResponse toResponse(Category category) {
		return new CategoryResponse(
				category.getId(),
				category.getName(),
				category.getDescription(),
				category.getCreatedAt(),
				category.getUpdatedAt()
		);
	}

	private String normalize(String value) {
		return value == null ? null : value.trim();
	}

	private String normalizeDescription(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
