package com.stockflow.service.impl;

import com.stockflow.dto.request.OutboundStockRequest;
import com.stockflow.dto.request.RestockProductRequest;
import com.stockflow.dto.request.StockMovementRequest;
import com.stockflow.dto.response.StockMovementResponse;
import com.stockflow.entity.Product;
import com.stockflow.entity.StockMovement;
import com.stockflow.entity.StockMovementType;
import com.stockflow.exception.InsufficientStockException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.StockMovementMapper;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
import com.stockflow.service.StockMovementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockMovementServiceImpl implements StockMovementService {

	private final StockMovementRepository stockMovementRepository;
	private final ProductRepository productRepository;
	private final StockMovementMapper stockMovementMapper;

	@Override
	@Transactional
	public StockMovementResponse createStockMovement(StockMovementRequest request) {
		Product product = findProductById(request.productId());
		applyMovement(product, request.type(), request.quantity());

		StockMovement movement = stockMovementMapper.toEntity(request, product);
		Product savedProduct = productRepository.save(product);
		StockMovement savedMovement = stockMovementRepository.save(movement);
		savedMovement.setProduct(savedProduct);
		return stockMovementMapper.toResponse(savedMovement);
	}

	@Override
	@Transactional
	public StockMovementResponse restockProduct(Long productId, RestockProductRequest request) {
		return createStockMovement(new StockMovementRequest(
				productId,
				StockMovementType.IN,
				request.quantity(),
				request.reason(),
				request.movementDate()
		));
	}

	@Override
	@Transactional
	public StockMovementResponse createOutboundStock(Long productId, OutboundStockRequest request) {
		return createStockMovement(new StockMovementRequest(
				productId,
				StockMovementType.OUT,
				request.quantity(),
				request.reason(),
				request.movementDate()
		));
	}

	@Override
	public List<StockMovementResponse> getAllStockMovements() {
		return stockMovementRepository.findAll().stream()
				.map(stockMovementMapper::toResponse)
				.toList();
	}

	@Override
	public StockMovementResponse getStockMovementById(Long id) {
		return stockMovementMapper.toResponse(findMovementById(id));
	}

	@Override
	public List<StockMovementResponse> getStockMovementsByProductId(Long productId) {
		findProductById(productId);
		return stockMovementRepository.findByProductIdOrderByMovementDateDesc(productId).stream()
				.map(stockMovementMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public StockMovementResponse updateStockMovement(Long id, StockMovementRequest request) {
		StockMovement existingMovement = findMovementById(id);
		Product originalProduct = existingMovement.getProduct();
		revertMovement(originalProduct, existingMovement.getType(), existingMovement.getQuantity());

		Product targetProduct = findProductById(request.productId());
		applyMovement(targetProduct, request.type(), request.quantity());

		productRepository.save(originalProduct);
		Product savedTargetProduct = targetProduct.getId().equals(originalProduct.getId())
				? originalProduct
				: productRepository.save(targetProduct);

		stockMovementMapper.updateEntity(existingMovement, request, savedTargetProduct);
		StockMovement updatedMovement = stockMovementRepository.save(existingMovement);
		return stockMovementMapper.toResponse(updatedMovement);
	}

	@Override
	@Transactional
	public void deleteStockMovement(Long id) {
		StockMovement movement = findMovementById(id);
		Product product = movement.getProduct();
		revertMovement(product, movement.getType(), movement.getQuantity());
		productRepository.save(product);
		stockMovementRepository.delete(movement);
	}

	private StockMovement findMovementById(Long id) {
		return stockMovementRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with id: " + id));
	}

	private Product findProductById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
	}

	private void applyMovement(Product product, StockMovementType type, int quantity) {
		if (type == StockMovementType.IN) {
			product.setQuantityInStock(product.getQuantityInStock() + quantity);
			return;
		}

		int updatedQuantity = product.getQuantityInStock() - quantity;
		if (updatedQuantity < 0) {
			throw new InsufficientStockException("Insufficient stock for product id: " + product.getId());
		}
		product.setQuantityInStock(updatedQuantity);
	}

	private void revertMovement(Product product, StockMovementType type, int quantity) {
		if (type == StockMovementType.IN) {
			int updatedQuantity = product.getQuantityInStock() - quantity;
			if (updatedQuantity < 0) {
				throw new InsufficientStockException("Cannot revert stock movement for product id: " + product.getId());
			}
			product.setQuantityInStock(updatedQuantity);
			return;
		}

		product.setQuantityInStock(product.getQuantityInStock() + quantity);
	}
}
