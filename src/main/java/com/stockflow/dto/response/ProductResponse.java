package com.stockflow.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
		Long id,
		String sku,
		String name,
		String description,
		BigDecimal purchasePrice,
		BigDecimal sellingPrice,
		Long categoryId,
		String categoryName,
		Long supplierId,
		String supplierName,
		Instant createdAt,
		Instant updatedAt
) {
}
