package com.stockflow.service;

import com.stockflow.dto.request.StockMovementRequest;
import com.stockflow.dto.response.StockMovementResponse;
import java.util.List;

public interface StockMovementService {

	StockMovementResponse createStockMovement(StockMovementRequest request);

	List<StockMovementResponse> getAllStockMovements();

	StockMovementResponse getStockMovementById(Long id);

	StockMovementResponse updateStockMovement(Long id, StockMovementRequest request);

	void deleteStockMovement(Long id);
}
