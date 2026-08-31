package com.stockflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockflow.dto.request.SupplierRequest;
import com.stockflow.dto.response.SupplierResponse;
import com.stockflow.entity.Supplier;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.SupplierMapper;
import com.stockflow.repository.SupplierRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTests {

	@Mock
	private SupplierRepository supplierRepository;

	private SupplierServiceImpl supplierService;

	@BeforeEach
	void setUp() {
		supplierService = new SupplierServiceImpl(supplierRepository, new SupplierMapper());
	}

	@Test
	void shouldCreateSupplier() {
		SupplierRequest request = new SupplierRequest(
				"Fresh Foods",
				"Jane Doe",
				"contact@freshfoods.com",
				"+33123456789",
				"12 Market Street"
		);
		Supplier savedSupplier = buildSupplier(1L, "Fresh Foods", "Jane Doe", "contact@freshfoods.com");

		when(supplierRepository.existsByNameIgnoreCase("Fresh Foods")).thenReturn(false);
		when(supplierRepository.existsByEmailIgnoreCase("contact@freshfoods.com")).thenReturn(false);
		when(supplierRepository.save(any(Supplier.class))).thenReturn(savedSupplier);

		SupplierResponse response = supplierService.createSupplier(request);

		assertNotNull(response);
		assertEquals(1L, response.id());
		assertEquals("Fresh Foods", response.name());
		assertEquals("contact@freshfoods.com", response.email());
		verify(supplierRepository).save(any(Supplier.class));
	}

	@Test
	void shouldRejectDuplicateSupplierNameOnCreate() {
		SupplierRequest request = new SupplierRequest(
				"Fresh Foods",
				"Jane Doe",
				"contact@freshfoods.com",
				"+33123456789",
				"12 Market Street"
		);

		when(supplierRepository.existsByNameIgnoreCase("Fresh Foods")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> supplierService.createSupplier(request));
		verify(supplierRepository, never()).save(any(Supplier.class));
	}

	@Test
	void shouldRejectDuplicateSupplierEmailOnCreate() {
		SupplierRequest request = new SupplierRequest(
				"Fresh Foods",
				"Jane Doe",
				"contact@freshfoods.com",
				"+33123456789",
				"12 Market Street"
		);

		when(supplierRepository.existsByNameIgnoreCase("Fresh Foods")).thenReturn(false);
		when(supplierRepository.existsByEmailIgnoreCase("contact@freshfoods.com")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> supplierService.createSupplier(request));
		verify(supplierRepository, never()).save(any(Supplier.class));
	}

	@Test
	void shouldReturnAllSuppliers() {
		Supplier first = buildSupplier(1L, "Fresh Foods", "Jane Doe", "contact@freshfoods.com");
		Supplier second = buildSupplier(2L, "Global Drinks", "John Smith", "sales@globaldrinks.com");

		when(supplierRepository.findAll()).thenReturn(List.of(first, second));

		List<SupplierResponse> responses = supplierService.getAllSuppliers();

		assertEquals(2, responses.size());
		assertEquals("Fresh Foods", responses.getFirst().name());
		assertEquals("Global Drinks", responses.get(1).name());
	}

	@Test
	void shouldReturnSupplierById() {
		Supplier supplier = buildSupplier(1L, "Fresh Foods", "Jane Doe", "contact@freshfoods.com");

		when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

		SupplierResponse response = supplierService.getSupplierById(1L);

		assertEquals(1L, response.id());
		assertEquals("Fresh Foods", response.name());
	}

	@Test
	void shouldThrowWhenSupplierNotFound() {
		when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById(99L));
	}

	@Test
	void shouldUpdateSupplier() {
		Supplier existingSupplier = buildSupplier(1L, "Fresh Foods", "Jane Doe", "contact@freshfoods.com");
		Supplier updatedSupplier = buildSupplier(1L, "Fresh Foods Europe", "Jane Doe", "europe@freshfoods.com");
		SupplierRequest request = new SupplierRequest(
				"Fresh Foods Europe",
				"Jane Doe",
				"europe@freshfoods.com",
				"+33987654321",
				"48 Supply Avenue"
		);

		when(supplierRepository.findById(1L)).thenReturn(Optional.of(existingSupplier));
		when(supplierRepository.existsByNameIgnoreCaseAndIdNot("Fresh Foods Europe", 1L)).thenReturn(false);
		when(supplierRepository.existsByEmailIgnoreCaseAndIdNot("europe@freshfoods.com", 1L)).thenReturn(false);
		when(supplierRepository.save(existingSupplier)).thenReturn(updatedSupplier);

		SupplierResponse response = supplierService.updateSupplier(1L, request);

		assertEquals("Fresh Foods Europe", response.name());
		assertEquals("europe@freshfoods.com", response.email());
	}

	@Test
	void shouldDeleteSupplier() {
		Supplier supplier = buildSupplier(1L, "Fresh Foods", "Jane Doe", "contact@freshfoods.com");

		when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

		supplierService.deleteSupplier(1L);

		verify(supplierRepository).delete(supplier);
	}

	private Supplier buildSupplier(Long id, String name, String contactName, String email) {
		Supplier supplier = new Supplier();
		supplier.setId(id);
		supplier.setName(name);
		supplier.setContactName(contactName);
		supplier.setEmail(email);
		supplier.setPhone("+33123456789");
		supplier.setAddress("12 Market Street");
		supplier.setCreatedAt(Instant.parse("2026-08-30T10:15:30Z"));
		supplier.setUpdatedAt(Instant.parse("2026-08-30T10:15:30Z"));
		return supplier;
	}
}
