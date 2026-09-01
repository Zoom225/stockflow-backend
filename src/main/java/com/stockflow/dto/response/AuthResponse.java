package com.stockflow.dto.response;

public record AuthResponse(
		String accessToken,
		String tokenType,
		Long userId,
		String fullName,
		String email,
		String role
) {
}
