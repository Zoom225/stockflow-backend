package com.stockflow.dto.response;

import com.stockflow.entity.StockMovementType;
import java.time.Instant;

public record StockMovementResponse(
		Long id,
		Long productId,
		String productName,
		String productSku,
		StockMovementType type,
		Integer quantity,
		String reason,
		Instant movementDate,
		Instant createdAt,
		Instant updatedAt
) {
}
