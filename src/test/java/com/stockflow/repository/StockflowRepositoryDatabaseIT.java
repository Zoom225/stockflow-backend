package com.stockflow.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stockflow.entity.AppUser;
import com.stockflow.entity.Category;
import com.stockflow.entity.Product;
import com.stockflow.entity.StockMovement;
import com.stockflow.entity.StockMovementType;
import com.stockflow.entity.Supplier;
import com.stockflow.entity.UserRole;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataJpaTest(properties = {
		"spring.flyway.enabled=true",
		"spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StockflowRepositoryDatabaseIT {

	@Container
	static final PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StockMovementRepository stockMovementRepository;

	@Autowired
	private SupplierRepository supplierRepository;

	@Autowired
	private UserRepository userRepository;

	@DynamicPropertySource
	static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Test
	void shouldValidateFlywaySchemaAndProductRepositoryQueriesAgainstPostgres() {
		Category category = saveCategory("Food");
		Supplier supplier = saveSupplier("Acme Foods", "contact@acme.test");

		Product oliveOil = saveProduct("SKU-001", "Olive Oil", 3, 5, category, supplier);
		saveProduct("SKU-002", "Rice", 10, 5, category, null);
		Product pasta = saveProduct("SKU-003", "Pasta", 5, 5, category, supplier);

		assertThat(productRepository.existsBySkuIgnoreCase("sku-001")).isTrue();
		assertThat(productRepository.sumTotalQuantityInStock()).isEqualTo(18);
		assertThat(productRepository.findByQuantityInStockLessThanEqualMinimumStockOrderByQuantityInStockAscNameAsc())
				.extracting(Product::getId)
				.containsExactly(oliveOil.getId(), pasta.getId());
		assertThat(productRepository.countByQuantityInStockLessThanEqualMinimumStock()).isEqualTo(2);
	}

	@Test
	void shouldEnforceDatabaseUniquenessConstraints() {
		Category category = saveCategory("Hardware");

		saveProduct("DUP-001", "Hammer", 4, 1, category, null);

		assertThatThrownBy(() -> saveProduct("DUP-001", "Another Hammer", 8, 1, category, null))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void shouldOrderStockMovementQueriesByMovementDate() {
		Category category = saveCategory("Beverages");
		Product product = saveProduct("DRINK-001", "Sparkling Water", 20, 5, category, null);
		Product otherProduct = saveProduct("DRINK-002", "Still Water", 20, 5, category, null);

		StockMovement older = saveMovement(product, StockMovementType.IN, 10, "Initial stock",
				Instant.parse("2026-08-30T10:00:00Z"));
		StockMovement newest = saveMovement(product, StockMovementType.OUT, 3, "Sale",
				Instant.parse("2026-09-01T10:00:00Z"));
		saveMovement(otherProduct, StockMovementType.IN, 5, "Other product",
				Instant.parse("2026-09-02T10:00:00Z"));

		assertThat(stockMovementRepository.existsByProductId(product.getId())).isTrue();
		assertThat(stockMovementRepository.findByProductIdOrderByMovementDateDesc(product.getId()))
				.extracting(StockMovement::getId)
				.containsExactly(newest.getId(), older.getId());
		assertThat(stockMovementRepository.findTop5ByOrderByMovementDateDesc())
				.extracting(StockMovement::getProduct)
				.extracting(Product::getId)
				.startsWith(otherProduct.getId(), product.getId());
	}

	@Test
	void shouldPersistAdminUsersAfterRoleMigrationAndFindEmailIgnoringCase() {
		AppUser user = new AppUser();
		user.setFullName("Admin User");
		user.setEmail("Admin@Example.test");
		user.setPasswordHash("{noop}secret");
		user.setRole(UserRole.ROLE_ADMIN);

		userRepository.saveAndFlush(user);

		assertThat(userRepository.existsByEmailIgnoreCase("admin@example.test")).isTrue();
		assertThat(userRepository.findByEmailIgnoreCase("ADMIN@EXAMPLE.TEST"))
				.isPresent()
				.get()
				.extracting(AppUser::getRole)
				.isEqualTo(UserRole.ROLE_ADMIN);
	}

	private Category saveCategory(String name) {
		Category category = new Category();
		category.setName(name);
		category.setDescription(name + " products");
		return categoryRepository.saveAndFlush(category);
	}

	private Supplier saveSupplier(String name, String email) {
		Supplier supplier = new Supplier();
		supplier.setName(name);
		supplier.setContactName("Jane Doe");
		supplier.setEmail(email);
		supplier.setPhone("+33123456789");
		supplier.setAddress("1 Main Street");
		return supplierRepository.saveAndFlush(supplier);
	}

	private Product saveProduct(String sku, String name, int quantityInStock, int minimumStock, Category category,
			Supplier supplier) {
		Product product = new Product();
		product.setSku(sku);
		product.setName(name);
		product.setDescription(name + " description");
		product.setPurchasePrice(new BigDecimal("4.50"));
		product.setSellingPrice(new BigDecimal("7.90"));
		product.setQuantityInStock(quantityInStock);
		product.setMinimumStock(minimumStock);
		product.setCategory(category);
		product.setSupplier(supplier);
		return productRepository.saveAndFlush(product);
	}

	private StockMovement saveMovement(Product product, StockMovementType type, int quantity, String reason,
			Instant movementDate) {
		StockMovement movement = new StockMovement();
		movement.setProduct(product);
		movement.setType(type);
		movement.setQuantity(quantity);
		movement.setReason(reason);
		movement.setMovementDate(movementDate);
		return stockMovementRepository.saveAndFlush(movement);
	}
}
