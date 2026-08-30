package com.stockflow.controller;

import com.stockflow.dto.request.SupplierRequest;
import com.stockflow.dto.response.SupplierResponse;
import com.stockflow.service.SupplierService;
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
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

	private final SupplierService supplierService;

	@PostMapping
	public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
		SupplierResponse createdSupplier = supplierService.createSupplier(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
	}

	@GetMapping
	public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
		return ResponseEntity.ok(supplierService.getAllSuppliers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
		return ResponseEntity.ok(supplierService.getSupplierById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SupplierResponse> updateSupplier(
			@PathVariable Long id,
			@Valid @RequestBody SupplierRequest request
	) {
		return ResponseEntity.ok(supplierService.updateSupplier(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
		supplierService.deleteSupplier(id);
		return ResponseEntity.noContent().build();
	}
}
