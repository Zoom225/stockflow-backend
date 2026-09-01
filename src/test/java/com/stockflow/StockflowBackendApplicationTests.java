package com.stockflow;

import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import com.stockflow.repository.SupplierRepository;
import com.stockflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration," +
				"org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration," +
				"org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration," +
				"org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration",
		"jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWYwMTIzNDU2Nzg5YWJjZGVm",
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

	@Test
	void contextLoads() {
	}

}
