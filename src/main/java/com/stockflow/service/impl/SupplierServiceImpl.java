package com.stockflow.service.impl;

import com.stockflow.dto.request.SupplierRequest;
import com.stockflow.dto.response.SupplierResponse;
import com.stockflow.entity.Supplier;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.SupplierMapper;
import com.stockflow.repository.SupplierRepository;
import com.stockflow.service.SupplierService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierServiceImpl implements SupplierService {

	private final SupplierRepository supplierRepository;
	private final SupplierMapper supplierMapper;

	@Override
	@Transactional
	public SupplierResponse createSupplier(SupplierRequest request) {
		validateUniqueFields(request, null);

		Supplier supplier = supplierMapper.toEntity(request);
		Supplier savedSupplier = supplierRepository.save(supplier);
		return supplierMapper.toResponse(savedSupplier);
	}

	@Override
	public List<SupplierResponse> getAllSuppliers() {
		return supplierRepository.findAll().stream()
				.map(supplierMapper::toResponse)
				.toList();
	}

	@Override
	public SupplierResponse getSupplierById(Long id) {
		return supplierMapper.toResponse(findSupplierById(id));
	}

	@Override
	@Transactional
	public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
		Supplier supplier = findSupplierById(id);
		validateUniqueFields(request, id);

		supplierMapper.updateEntity(supplier, request);
		Supplier updatedSupplier = supplierRepository.save(supplier);
		return supplierMapper.toResponse(updatedSupplier);
	}

	@Override
	@Transactional
	public void deleteSupplier(Long id) {
		Supplier supplier = findSupplierById(id);
		supplierRepository.delete(supplier);
	}

	private Supplier findSupplierById(Long id) {
		return supplierRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
	}

	private void validateUniqueFields(SupplierRequest request, Long supplierId) {
		String normalizedName = request.name() == null ? null : request.name().trim();
		String normalizedEmail = request.email() == null ? null : request.email().trim().toLowerCase();

		boolean nameExists = supplierId == null
				? supplierRepository.existsByNameIgnoreCase(normalizedName)
				: supplierRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, supplierId);

		if (nameExists) {
			throw new DuplicateResourceException("Supplier name already exists: " + normalizedName);
		}

		boolean emailExists = supplierId == null
				? supplierRepository.existsByEmailIgnoreCase(normalizedEmail)
				: supplierRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, supplierId);

		if (emailExists) {
			throw new DuplicateResourceException("Supplier email already exists: " + normalizedEmail);
		}
	}
}
