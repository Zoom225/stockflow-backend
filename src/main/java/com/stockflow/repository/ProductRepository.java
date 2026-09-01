package com.stockflow.repository;

import com.stockflow.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsBySkuIgnoreCase(String sku);

	boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

	List<Product> findByQuantityInStockLessThanEqualMinimumStockOrderByQuantityInStockAscNameAsc();

	long countByQuantityInStockLessThanEqualMinimumStock();

	@Query("select coalesce(sum(p.quantityInStock), 0) from Product p")
	long sumTotalQuantityInStock();
}
