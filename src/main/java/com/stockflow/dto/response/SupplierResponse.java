package com.stockflow.dto.response;

import java.time.Instant;

public record SupplierResponse(
		Long id,
		String name,
		String contactName,
		String email,
		String phone,
		String address,
		Instant createdAt,
		Instant updatedAt
) {
}
