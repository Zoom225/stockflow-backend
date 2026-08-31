package com.stockflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockflow.dto.request.StockMovementRequest;
import com.stockflow.dto.response.StockMovementResponse;
import com.stockflow.entity.Category;
import com.stockflow.entity.Product;
import com.stockflow.entity.StockMovement;
import com.stockflow.entity.StockMovementType;
import com.stockflow.exception.InsufficientStockException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.StockMovementMapper;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceImplTests {

	@Mock
	private StockMovementRepository stockMovementRepository;

	@Mock
	private ProductRepository productRepository;

	private StockMovementServiceImpl stockMovementService;

	@BeforeEach
	void setUp() {
		stockMovementService = new StockMovementServiceImpl(
				stockMovementRepository,
				productRepository,
				new StockMovementMapper()
		);
	}

	@Test
	void shouldCreateInboundMovementAndIncreaseStock() {
		Product product = buildProduct(1L, "SKU-001", "Olive Oil", 10);
		StockMovementRequest request = new StockMovementRequest(
				1L,
				StockMovementType.IN,
				5,
				"Restock",
				Instant.parse("2026-08-31T12:00:00Z")
		);
		StockMovement savedMovement = buildMovement(1L, product, StockMovementType.IN, 5);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productRepository.save(product)).thenReturn(product);
		when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(savedMovement);

		StockMovementResponse response = stockMovementService.createStockMovement(request);

		assertEquals(15, product.getQuantityInStock());
		assertEquals(StockMovementType.IN, response.type());
		verify(stockMovementRepository).save(any(StockMovement.class));
	}

	@Test
	void shouldRejectOutboundMovementWhenStockIsInsufficient() {
		Product product = buildProduct(1L, "SKU-001", "Olive Oil", 2);
		StockMovementRequest request = new StockMovementRequest(
				1L,
				StockMovementType.OUT,
				5,
				"Sale",
				Instant.parse("2026-08-31T12:00:00Z")
		);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		assertThrows(InsufficientStockException.class, () -> stockMovementService.createStockMovement(request));
		verify(stockMovementRepository, never()).save(any(StockMovement.class));
	}

	@Test
	void shouldReturnAllStockMovements() {
		Product product = buildProduct(1L, "SKU-001", "Olive Oil", 10);
		StockMovement first = buildMovement(1L, product, StockMovementType.IN, 5);
		StockMovement second = buildMovement(2L, product, StockMovementType.OUT, 2);

		when(stockMovementRepository.findAll()).thenReturn(List.of(first, second));

		List<StockMovementResponse> responses = stockMovementService.getAllStockMovements();

		assertEquals(2, responses.size());
		assertEquals(StockMovementType.IN, responses.getFirst().type());
		assertEquals(StockMovementType.OUT, responses.get(1).type());
	}

	@Test
	void shouldReturnMovementById() {
		Product product = buildProduct(1L, "SKU-001", "Olive Oil", 10);
		StockMovement movement = buildMovement(1L, product, StockMovementType.IN, 5);

		when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(movement));

		StockMovementResponse response = stockMovementService.getStockMovementById(1L);

		assertEquals(1L, response.id());
		assertEquals("SKU-001", response.productSku());
	}

	@Test
	void shouldUpdateMovementAndAdjustStock() {
		Product product = buildProduct(1L, "SKU-001", "Olive Oil", 15);
		StockMovement existingMovement = buildMovement(1L, product, StockMovementType.IN, 5);
		StockMovementRequest request = new StockMovementRequest(
				1L,
				StockMovementType.OUT,
				3,
				"Correction",
				Instant.parse("2026-08-31T13:00:00Z")
		);

		when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(existingMovement));
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productRepository.save(product)).thenReturn(product);
		when(stockMovementRepository.save(existingMovement)).thenReturn(existingMovement);

		StockMovementResponse response = stockMovementService.updateStockMovement(1L, request);

		assertEquals(7, product.getQuantityInStock());
		assertEquals(StockMovementType.OUT, response.type());
		assertEquals(3, response.quantity());
	}

	@Test
	void shouldDeleteMovementAndRevertStock() {
		Product product = buildProduct(1L, "SKU-001", "Olive Oil", 8);
		StockMovement movement = buildMovement(1L, product, StockMovementType.OUT, 2);

		when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(movement));
		when(productRepository.save(product)).thenReturn(product);

		stockMovementService.deleteStockMovement(1L);

		assertEquals(10, product.getQuantityInStock());
		verify(stockMovementRepository).delete(movement);
	}

	@Test
	void shouldThrowWhenMovementNotFound() {
		when(stockMovementRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> stockMovementService.getStockMovementById(99L));
	}

	private Product buildProduct(Long id, String sku, String name, int quantityInStock) {
		Category category = new Category();
		category.setId(1L);
		category.setName("Food");

		Product product = new Product();
		product.setId(id);
		product.setSku(sku);
		product.setName(name);
		product.setDescription("Sample description");
		product.setPurchasePrice(new BigDecimal("8.50"));
		product.setSellingPrice(new BigDecimal("12.90"));
		product.setQuantityInStock(quantityInStock);
		product.setCategory(category);
		product.setCreatedAt(Instant.parse("2026-08-31T10:15:30Z"));
		product.setUpdatedAt(Instant.parse("2026-08-31T10:15:30Z"));
		return product;
	}

	private StockMovement buildMovement(Long id, Product product, StockMovementType type, int quantity) {
		StockMovement movement = new StockMovement();
		movement.setId(id);
		movement.setProduct(product);
		movement.setType(type);
		movement.setQuantity(quantity);
		movement.setReason("Sample reason");
		movement.setMovementDate(Instant.parse("2026-08-31T12:00:00Z"));
		movement.setCreatedAt(Instant.parse("2026-08-31T12:00:00Z"));
		movement.setUpdatedAt(Instant.parse("2026-08-31T12:00:00Z"));
		return movement;
	}
}
