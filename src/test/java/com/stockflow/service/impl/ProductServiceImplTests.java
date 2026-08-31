package com.stockflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockflow.dto.request.ProductRequest;
import com.stockflow.dto.response.ProductResponse;
import com.stockflow.entity.Category;
import com.stockflow.entity.Product;
import com.stockflow.entity.Supplier;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.ProductMapper;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.SupplierRepository;
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
class ProductServiceImplTests {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private SupplierRepository supplierRepository;

	private ProductServiceImpl productService;

	@BeforeEach
	void setUp() {
		productService = new ProductServiceImpl(
				productRepository,
				categoryRepository,
				supplierRepository,
				new ProductMapper()
		);
	}

	@Test
	void shouldCreateProduct() {
		ProductRequest request = new ProductRequest(
				"SKU-001",
				"Olive Oil",
				"Extra virgin olive oil",
				new BigDecimal("8.50"),
				new BigDecimal("12.90"),
				1L,
				5,
				2L
		);
		Category category = buildCategory(1L, "Food");
		Supplier supplier = buildSupplier(2L, "Fresh Foods");
		Product savedProduct = buildProduct(1L, "SKU-001", "Olive Oil", category, supplier);

		when(productRepository.existsBySkuIgnoreCase("SKU-001")).thenReturn(false);
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));
		when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

		ProductResponse response = productService.createProduct(request);

		assertNotNull(response);
		assertEquals(1L, response.id());
		assertEquals("SKU-001", response.sku());
		assertEquals(5, response.minimumStock());
		assertEquals("Food", response.categoryName());
		assertEquals("Fresh Foods", response.supplierName());
		verify(productRepository).save(any(Product.class));
	}

	@Test
	void shouldRejectDuplicateProductSkuOnCreate() {
		ProductRequest request = new ProductRequest(
				"SKU-001",
				"Olive Oil",
				"Extra virgin olive oil",
				new BigDecimal("8.50"),
				new BigDecimal("12.90"),
				1L,
				5,
				2L
		);

		when(productRepository.existsBySkuIgnoreCase("SKU-001")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> productService.createProduct(request));
		verify(productRepository, never()).save(any(Product.class));
	}

	@Test
	void shouldThrowWhenCategoryNotFoundOnCreate() {
		ProductRequest request = new ProductRequest(
				"SKU-001",
				"Olive Oil",
				"Extra virgin olive oil",
				new BigDecimal("8.50"),
				new BigDecimal("12.90"),
				99L,
				5,
				null
		);

		when(productRepository.existsBySkuIgnoreCase("SKU-001")).thenReturn(false);
		when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(request));
		verify(productRepository, never()).save(any(Product.class));
	}

	@Test
	void shouldReturnAllProducts() {
		Category category = buildCategory(1L, "Food");
		Supplier supplier = buildSupplier(2L, "Fresh Foods");
		Product first = buildProduct(1L, "SKU-001", "Olive Oil", category, supplier);
		Product second = buildProduct(2L, "SKU-002", "Rice", category, null);

		when(productRepository.findAll()).thenReturn(List.of(first, second));

		List<ProductResponse> responses = productService.getAllProducts();

		assertEquals(2, responses.size());
		assertEquals("SKU-001", responses.getFirst().sku());
		assertEquals("Rice", responses.get(1).name());
	}

	@Test
	void shouldReturnLowStockProducts() {
		Category category = buildCategory(1L, "Food");
		Product lowStock = buildProduct(1L, "SKU-001", "Olive Oil", category, null);
		lowStock.setQuantityInStock(2);
		lowStock.setMinimumStock(5);

		Product healthyStock = buildProduct(2L, "SKU-002", "Rice", category, null);
		healthyStock.setQuantityInStock(20);
		healthyStock.setMinimumStock(5);

		when(productRepository.findByQuantityInStockLessThanEqualMinimumStockOrderByQuantityInStockAscNameAsc())
				.thenReturn(List.of(lowStock));

		List<ProductResponse> responses = productService.getLowStockProducts();

		assertEquals(1, responses.size());
		assertEquals("SKU-001", responses.getFirst().sku());
		assertEquals(true, responses.getFirst().lowStock());
	}

	@Test
	void shouldReturnProductById() {
		Category category = buildCategory(1L, "Food");
		Supplier supplier = buildSupplier(2L, "Fresh Foods");
		Product product = buildProduct(1L, "SKU-001", "Olive Oil", category, supplier);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		ProductResponse response = productService.getProductById(1L);

		assertEquals(1L, response.id());
		assertEquals("Olive Oil", response.name());
	}

	@Test
	void shouldUpdateProduct() {
		Category oldCategory = buildCategory(1L, "Food");
		Category newCategory = buildCategory(3L, "Beverages");
		Supplier supplier = buildSupplier(2L, "Fresh Foods");
		Product existingProduct = buildProduct(1L, "SKU-001", "Olive Oil", oldCategory, supplier);
		Product updatedProduct = buildProduct(1L, "SKU-003", "Sparkling Water", newCategory, null);
		updatedProduct.setMinimumStock(4);
		ProductRequest request = new ProductRequest(
				"SKU-003",
				"Sparkling Water",
				"Premium sparkling water",
				new BigDecimal("1.50"),
				new BigDecimal("2.90"),
				3L,
				4,
				null
		);

		when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
		when(productRepository.existsBySkuIgnoreCaseAndIdNot("SKU-003", 1L)).thenReturn(false);
		when(categoryRepository.findById(3L)).thenReturn(Optional.of(newCategory));
		when(productRepository.save(existingProduct)).thenReturn(updatedProduct);

		ProductResponse response = productService.updateProduct(1L, request);

		assertEquals("SKU-003", response.sku());
		assertEquals(4, response.minimumStock());
		assertEquals("Beverages", response.categoryName());
		assertEquals(null, response.supplierId());
	}

	@Test
	void shouldDeleteProduct() {
		Category category = buildCategory(1L, "Food");
		Product product = buildProduct(1L, "SKU-001", "Olive Oil", category, null);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		productService.deleteProduct(1L);

		verify(productRepository).delete(product);
	}

	private Product buildProduct(Long id, String sku, String name, Category category, Supplier supplier) {
		Product product = new Product();
		product.setId(id);
		product.setSku(sku);
		product.setName(name);
		product.setDescription("Sample description");
		product.setPurchasePrice(new BigDecimal("8.50"));
		product.setSellingPrice(new BigDecimal("12.90"));
		product.setQuantityInStock(0);
		product.setMinimumStock(5);
		product.setCategory(category);
		product.setSupplier(supplier);
		product.setCreatedAt(Instant.parse("2026-08-31T10:15:30Z"));
		product.setUpdatedAt(Instant.parse("2026-08-31T10:15:30Z"));
		return product;
	}

	private Category buildCategory(Long id, String name) {
		Category category = new Category();
		category.setId(id);
		category.setName(name);
		return category;
	}

	private Supplier buildSupplier(Long id, String name) {
		Supplier supplier = new Supplier();
		supplier.setId(id);
		supplier.setName(name);
		return supplier;
	}
}
