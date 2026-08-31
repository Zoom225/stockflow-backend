package com.stockflow.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
		@NotBlank(message = "Product SKU is required")
		@Size(max = 50, message = "Product SKU must not exceed 50 characters")
		String sku,

		@NotBlank(message = "Product name is required")
		@Size(max = 150, message = "Product name must not exceed 150 characters")
		String name,

		@Size(max = 255, message = "Product description must not exceed 255 characters")
		String description,

		@NotNull(message = "Purchase price is required")
		@DecimalMin(value = "0.00", inclusive = true, message = "Purchase price must be greater than or equal to 0")
		BigDecimal purchasePrice,

		@NotNull(message = "Selling price is required")
		@DecimalMin(value = "0.00", inclusive = true, message = "Selling price must be greater than or equal to 0")
		BigDecimal sellingPrice,

		@NotNull(message = "Category id is required")
		Long categoryId,

		Long supplierId
) {
}
