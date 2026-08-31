package com.stockflow.mapper;

import com.stockflow.dto.request.StockMovementRequest;
import com.stockflow.dto.response.StockMovementResponse;
import com.stockflow.entity.Product;
import com.stockflow.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

	public StockMovement toEntity(StockMovementRequest request, Product product) {
		StockMovement movement = new StockMovement();
		updateEntity(movement, request, product);
		return movement;
	}

	public void updateEntity(StockMovement movement, StockMovementRequest request, Product product) {
		movement.setProduct(product);
		movement.setType(request.type());
		movement.setQuantity(request.quantity());
		movement.setReason(normalizeOptional(request.reason()));
		movement.setMovementDate(request.movementDate());
	}

	public StockMovementResponse toResponse(StockMovement movement) {
		return new StockMovementResponse(
				movement.getId(),
				movement.getProduct().getId(),
				movement.getProduct().getName(),
				movement.getProduct().getSku(),
				movement.getType(),
				movement.getQuantity(),
				movement.getReason(),
				movement.getMovementDate(),
				movement.getCreatedAt(),
				movement.getUpdatedAt()
		);
	}

	private String normalizeOptional(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
