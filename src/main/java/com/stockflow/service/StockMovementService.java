package com.stockflow.service;

import com.stockflow.dto.request.RestockProductRequest;
import com.stockflow.dto.request.StockMovementRequest;
import com.stockflow.dto.response.StockMovementResponse;
import java.util.List;

public interface StockMovementService {

	StockMovementResponse createStockMovement(StockMovementRequest request);

	StockMovementResponse restockProduct(Long productId, RestockProductRequest request);

	List<StockMovementResponse> getAllStockMovements();

	StockMovementResponse getStockMovementById(Long id);

	List<StockMovementResponse> getStockMovementsByProductId(Long productId);

	StockMovementResponse updateStockMovement(Long id, StockMovementRequest request);

	void deleteStockMovement(Long id);
}
