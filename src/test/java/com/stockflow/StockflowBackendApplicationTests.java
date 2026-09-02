package com.stockflow;

import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import com.stockflow.repository.SupplierRepository;
import com.stockflow.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration," +
				"org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration," +
				"org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration," +
				"org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration",
		"jwt.expiration-ms=86400000"
})
class StockflowBackendApplicationTests {

	@MockitoBean
	private CategoryRepository categoryRepository;

	@MockitoBean
	private SupplierRepository supplierRepository;

	@MockitoBean
	private ProductRepository productRepository;

	@MockitoBean
	private StockMovementRepository stockMovementRepository;

	@MockitoBean
	private UserRepository userRepository;

	@DynamicPropertySource
	static void registerJwtProperties(DynamicPropertyRegistry registry) {
		registry.add("jwt.secret", StockflowBackendApplicationTests::testJwtSecret);
	}

	@Test
	void contextLoads() {
	}

	private static String testJwtSecret() {
		return Base64.getEncoder()
				.encodeToString("stockflow-context-test-jwt-signing-key-not-for-real-use".getBytes(StandardCharsets.UTF_8));
	}

}
