package com.stockflow.service.impl;

import com.stockflow.dto.request.ProductRequest;
import com.stockflow.dto.response.ProductResponse;
import com.stockflow.entity.Category;
import com.stockflow.entity.Product;
import com.stockflow.entity.Supplier;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.ProductMapper;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.SupplierRepository;
import com.stockflow.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final SupplierRepository supplierRepository;
	private final ProductMapper productMapper;

	@Override
	@Transactional
	public ProductResponse createProduct(ProductRequest request) {
		validateUniqueSku(request.sku(), null);

		Category category = findCategoryById(request.categoryId());
		Supplier supplier = findSupplierByIdOrNull(request.supplierId());

		Product product = productMapper.toEntity(request, category, supplier);
		Product savedProduct = productRepository.save(product);
		return productMapper.toResponse(savedProduct);
	}

	@Override
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream()
				.map(productMapper::toResponse)
				.toList();
	}

	@Override
	public List<ProductResponse> getLowStockProducts() {
		return productRepository.findByQuantityInStockLessThanEqualMinimumStockOrderByQuantityInStockAscNameAsc().stream()
				.map(productMapper::toResponse)
				.toList();
	}

	@Override
	public ProductResponse getProductById(Long id) {
		return productMapper.toResponse(findProductById(id));
	}

	@Override
	@Transactional
	public ProductResponse updateProduct(Long id, ProductRequest request) {
		Product product = findProductById(id);
		validateUniqueSku(request.sku(), id);

		Category category = findCategoryById(request.categoryId());
		Supplier supplier = findSupplierByIdOrNull(request.supplierId());

		productMapper.updateEntity(product, request, category, supplier);
		Product updatedProduct = productRepository.save(product);
		return productMapper.toResponse(updatedProduct);
	}

	@Override
	@Transactional
	public void deleteProduct(Long id) {
		Product product = findProductById(id);
		productRepository.delete(product);
	}

	private Product findProductById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
	}

	private Category findCategoryById(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
	}

	private Supplier findSupplierByIdOrNull(Long id) {
		if (id == null) {
			return null;
		}

		return supplierRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
	}

	private void validateUniqueSku(String sku, Long productId) {
		String normalizedSku = sku == null ? null : sku.trim();
		boolean exists = productId == null
				? productRepository.existsBySkuIgnoreCase(normalizedSku)
				: productRepository.existsBySkuIgnoreCaseAndIdNot(normalizedSku, productId);

		if (exists) {
			throw new DuplicateResourceException("Product SKU already exists: " + normalizedSku);
		}
	}
}
