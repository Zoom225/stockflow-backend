package com.stockflow.dto.response;

import java.util.List;

public record DashboardSummaryResponse(
		long totalProducts,
		long totalCategories,
		long totalSuppliers,
		long lowStockProducts,
		long totalStockQuantity,
		List<DashboardRecentMovementResponse> recentStockMovements
) {
}
