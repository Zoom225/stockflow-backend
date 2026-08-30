package com.stockflow.service;

import com.stockflow.dto.request.SupplierRequest;
import com.stockflow.dto.response.SupplierResponse;
import java.util.List;

public interface SupplierService {

	SupplierResponse createSupplier(SupplierRequest request);

	List<SupplierResponse> getAllSuppliers();

	SupplierResponse getSupplierById(Long id);

	SupplierResponse updateSupplier(Long id, SupplierRequest request);

	void deleteSupplier(Long id);
}
