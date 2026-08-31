package com.stockflow.repository;

import com.stockflow.entity.StockMovement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

	List<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId);
}
