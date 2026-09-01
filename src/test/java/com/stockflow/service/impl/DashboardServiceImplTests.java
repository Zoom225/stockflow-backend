package com.stockflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.stockflow.dto.response.DashboardSummaryResponse;
import com.stockflow.entity.Category;
import com.stockflow.entity.Product;
import com.stockflow.entity.StockMovement;
import com.stockflow.entity.StockMovementType;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import com.stockflow.repository.SupplierRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTests {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private SupplierRepository supplierRepository;

	@Mock
	private StockMovementRepository stockMovementRepository;

	private DashboardServiceImpl dashboardService;

	@BeforeEach
	void setUp() {
		dashboardService = new DashboardServiceImpl(
				productRepository,
				categoryRepository,
				supplierRepository,
				stockMovementRepository
		);
	}

	@Test
	void shouldReturnDashboardSummary() {
		Product product = buildProduct(1L, "SKU-001", "Olive Oil");
		StockMovement movement = buildMovement(10L, product, StockMovementType.IN, 8);

		when(productRepository.count()).thenReturn(12L);
		when(categoryRepository.count()).thenReturn(4L);
		when(supplierRepository.count()).thenReturn(6L);
		when(productRepository.countByQuantityInStockLessThanEqualMinimumStock()).thenReturn(3L);
		when(productRepository.sumTotalQuantityInStock()).thenReturn(125L);
		when(stockMovementRepository.findTop5ByOrderByMovementDateDesc()).thenReturn(List.of(movement));

		DashboardSummaryResponse response = dashboardService.getDashboardSummary();

		assertEquals(12L, response.totalProducts());
		assertEquals(4L, response.totalCategories());
		assertEquals(6L, response.totalSuppliers());
		assertEquals(3L, response.lowStockProducts());
		assertEquals(125L, response.totalStockQuantity());
		assertEquals(1, response.recentStockMovements().size());
		assertEquals("SKU-001", response.recentStockMovements().getFirst().productSku());
	}

	private Product buildProduct(Long id, String sku, String name) {
		Category category = new Category();
		category.setId(1L);
		category.setName("Food");

		Product product = new Product();
		product.setId(id);
		product.setSku(sku);
		product.setName(name);
		product.setDescription("Sample");
		product.setPurchasePrice(new BigDecimal("10.00"));
		product.setSellingPrice(new BigDecimal("15.00"));
		product.setQuantityInStock(20);
		product.setMinimumStock(5);
		product.setCategory(category);
		product.setCreatedAt(Instant.parse("2026-09-01T10:00:00Z"));
		product.setUpdatedAt(Instant.parse("2026-09-01T10:00:00Z"));
		return product;
	}

	private StockMovement buildMovement(Long id, Product product, StockMovementType type, int quantity) {
		StockMovement movement = new StockMovement();
		movement.setId(id);
		movement.setProduct(product);
		movement.setType(type);
		movement.setQuantity(quantity);
		movement.setMovementDate(Instant.parse("2026-09-01T12:00:00Z"));
		movement.setCreatedAt(Instant.parse("2026-09-01T12:00:00Z"));
		movement.setUpdatedAt(Instant.parse("2026-09-01T12:00:00Z"));
		return movement;
	}
}
