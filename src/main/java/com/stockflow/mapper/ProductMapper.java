package com.stockflow.mapper;

import com.stockflow.dto.request.ProductRequest;
import com.stockflow.dto.response.ProductResponse;
import com.stockflow.entity.Category;
import com.stockflow.entity.Product;
import com.stockflow.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

	public Product toEntity(ProductRequest request, Category category, Supplier supplier) {
		Product product = new Product();
		updateEntity(product, request, category, supplier);
		return product;
	}

	public void updateEntity(Product product, ProductRequest request, Category category, Supplier supplier) {
		product.setSku(normalize(request.sku()));
		product.setName(normalize(request.name()));
		product.setDescription(normalizeOptional(request.description()));
		product.setPurchasePrice(request.purchasePrice());
		product.setSellingPrice(request.sellingPrice());
		product.setCategory(category);
		product.setSupplier(supplier);
	}

	public ProductResponse toResponse(Product product) {
		return new ProductResponse(
				product.getId(),
				product.getSku(),
				product.getName(),
				product.getDescription(),
				product.getPurchasePrice(),
				product.getSellingPrice(),
				product.getQuantityInStock(),
				product.getCategory().getId(),
				product.getCategory().getName(),
				product.getSupplier() != null ? product.getSupplier().getId() : null,
				product.getSupplier() != null ? product.getSupplier().getName() : null,
				product.getCreatedAt(),
				product.getUpdatedAt()
		);
	}

	private String normalize(String value) {
		return value == null ? null : value.trim();
	}

	private String normalizeOptional(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
