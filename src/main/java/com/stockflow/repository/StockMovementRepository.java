package com.stockflow.repository;

import com.stockflow.entity.StockMovement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

	boolean existsByProductId(Long productId);

	List<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId);

	List<StockMovement> findTop5ByOrderByMovementDateDesc();
}
