package com.stockflow.service.impl;

import com.stockflow.dto.response.DashboardRecentMovementResponse;
import com.stockflow.dto.response.DashboardSummaryResponse;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import com.stockflow.repository.SupplierRepository;
import com.stockflow.service.DashboardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final SupplierRepository supplierRepository;
	private final StockMovementRepository stockMovementRepository;

	@Override
	public DashboardSummaryResponse getDashboardSummary() {
		// Regle metier : le tableau de bord doit exposer une synthese globale exploitable directement par le front.
		long totalProducts = productRepository.count();
		long totalCategories = categoryRepository.count();
		long totalSuppliers = supplierRepository.count();
		long lowStockProducts = productRepository.countByQuantityInStockLessThanEqualMinimumStock();
		long totalStockQuantity = productRepository.sumTotalQuantityInStock();

		// Regle metier : les derniers mouvements permettent de visualiser rapidement l'activite recente du stock.
		List<DashboardRecentMovementResponse> recentStockMovements = stockMovementRepository
				.findTop5ByOrderByMovementDateDesc()
				.stream()
				.map(movement -> new DashboardRecentMovementResponse(
						movement.getId(),
						movement.getProduct().getId(),
						movement.getProduct().getName(),
						movement.getProduct().getSku(),
						movement.getType(),
						movement.getQuantity(),
						movement.getMovementDate()
				))
				.toList();

		return new DashboardSummaryResponse(
				totalProducts,
				totalCategories,
				totalSuppliers,
				lowStockProducts,
				totalStockQuantity,
				recentStockMovements
		);
	}
}
