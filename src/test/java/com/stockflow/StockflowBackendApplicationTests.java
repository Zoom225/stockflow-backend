package com.stockflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration," +
				"org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration," +
				"org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration," +
				"org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
})
class StockflowBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
