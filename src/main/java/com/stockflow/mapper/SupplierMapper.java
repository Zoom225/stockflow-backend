package com.stockflow.mapper;

import com.stockflow.dto.request.SupplierRequest;
import com.stockflow.dto.response.SupplierResponse;
import com.stockflow.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

	public Supplier toEntity(SupplierRequest request) {
		Supplier supplier = new Supplier();
		supplier.setName(normalize(request.name()));
		supplier.setContactName(normalize(request.contactName()));
		supplier.setEmail(normalizeEmail(request.email()));
		supplier.setPhone(normalizeOptional(request.phone()));
		supplier.setAddress(normalizeOptional(request.address()));
		return supplier;
	}

	public void updateEntity(Supplier supplier, SupplierRequest request) {
		supplier.setName(normalize(request.name()));
		supplier.setContactName(normalize(request.contactName()));
		supplier.setEmail(normalizeEmail(request.email()));
		supplier.setPhone(normalizeOptional(request.phone()));
		supplier.setAddress(normalizeOptional(request.address()));
	}

	public SupplierResponse toResponse(Supplier supplier) {
		return new SupplierResponse(
				supplier.getId(),
				supplier.getName(),
				supplier.getContactName(),
				supplier.getEmail(),
				supplier.getPhone(),
				supplier.getAddress(),
				supplier.getCreatedAt(),
				supplier.getUpdatedAt()
		);
	}

	private String normalize(String value) {
		return value == null ? null : value.trim();
	}

	private String normalizeEmail(String value) {
		return value == null ? null : value.trim().toLowerCase();
	}

	private String normalizeOptional(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
