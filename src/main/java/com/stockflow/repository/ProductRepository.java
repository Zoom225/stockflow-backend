package com.stockflow.repository;

import com.stockflow.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsBySkuIgnoreCase(String sku);

	boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

	List<Product> findByQuantityInStockLessThanEqualMinimumStockOrderByQuantityInStockAscNameAsc();
}
