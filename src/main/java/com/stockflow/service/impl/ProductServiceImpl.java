package com.stockflow.service.impl;

import com.stockflow.dto.request.ProductRequest;
import com.stockflow.dto.response.ProductResponse;
import com.stockflow.entity.Category;
import com.stockflow.entity.Product;
import com.stockflow.entity.Supplier;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ProductDeletionNotAllowedException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.ProductMapper;
import com.stockflow.repository.CategoryRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.repository.StockMovementRepository;
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
	private final StockMovementRepository stockMovementRepository;
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
		// Regle metier : un produit est en alerte quand son stock courant est inferieur ou egal au stock minimum.
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

		// Regle metier : un produit ayant deja un historique de mouvements ne doit pas etre supprime.
		if (stockMovementRepository.existsByProductId(id)) {
			throw new ProductDeletionNotAllowedException(
					"Suppression impossible : ce produit possede deja un historique de mouvements de stock."
			);
		}

		productRepository.delete(product);
	}

	private Product findProductById(Long id) {
		// Regle metier : un produit doit exister avant toute lecture, modification ou suppression.
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'identifiant : " + id));
	}

	private Category findCategoryById(Long id) {
		// Regle metier : un produit doit toujours etre rattache a une categorie existante.
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categorie introuvable avec l'identifiant : " + id));
	}

	private Supplier findSupplierByIdOrNull(Long id) {
		if (id == null) {
			return null;
		}

		// Regle metier : si un fournisseur est renseigne sur le produit, il doit exister.
		return supplierRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable avec l'identifiant : " + id));
	}

	private void validateUniqueSku(String sku, Long productId) {
		String normalizedSku = sku == null ? null : sku.trim();

		// Regle metier : le SKU identifie un produit de facon unique dans le stock.
		boolean exists = productId == null
				? productRepository.existsBySkuIgnoreCase(normalizedSku)
				: productRepository.existsBySkuIgnoreCaseAndIdNot(normalizedSku, productId);

		if (exists) {
			throw new DuplicateResourceException("Le SKU du produit existe deja : " + normalizedSku);
		}
	}
}
