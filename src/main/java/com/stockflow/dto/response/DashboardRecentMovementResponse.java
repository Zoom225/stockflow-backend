package com.stockflow.dto.response;

import com.stockflow.entity.StockMovementType;
import java.time.Instant;

public record DashboardRecentMovementResponse(
		Long id,
		Long productId,
		String productName,
		String productSku,
		StockMovementType type,
		Integer quantity,
		Instant movementDate
) {
}
