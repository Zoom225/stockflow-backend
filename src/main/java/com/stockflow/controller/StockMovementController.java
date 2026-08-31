package com.stockflow.controller;

import com.stockflow.dto.request.StockMovementRequest;
import com.stockflow.dto.response.StockMovementResponse;
import com.stockflow.service.StockMovementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

	private final StockMovementService stockMovementService;

	@PostMapping
	public ResponseEntity<StockMovementResponse> createStockMovement(@Valid @RequestBody StockMovementRequest request) {
		StockMovementResponse createdMovement = stockMovementService.createStockMovement(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdMovement);
	}

	@GetMapping
	public ResponseEntity<List<StockMovementResponse>> getAllStockMovements() {
		return ResponseEntity.ok(stockMovementService.getAllStockMovements());
	}

	@GetMapping("/products/{productId}")
	public ResponseEntity<List<StockMovementResponse>> getStockMovementsByProductId(@PathVariable Long productId) {
		return ResponseEntity.ok(stockMovementService.getStockMovementsByProductId(productId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<StockMovementResponse> getStockMovementById(@PathVariable Long id) {
		return ResponseEntity.ok(stockMovementService.getStockMovementById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<StockMovementResponse> updateStockMovement(
			@PathVariable Long id,
			@Valid @RequestBody StockMovementRequest request
	) {
		return ResponseEntity.ok(stockMovementService.updateStockMovement(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {
		stockMovementService.deleteStockMovement(id);
		return ResponseEntity.noContent().build();
	}
}
