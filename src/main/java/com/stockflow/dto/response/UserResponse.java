package com.stockflow.dto.response;

import java.time.Instant;

public record UserResponse(
		Long id,
		String fullName,
		String email,
		String role,
		Instant createdAt,
		Instant updatedAt
) {
}
