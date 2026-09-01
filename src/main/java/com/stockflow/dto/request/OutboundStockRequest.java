package com.stockflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record OutboundStockRequest(
		@NotNull(message = "Quantity is required")
		@Min(value = 1, message = "Quantity must be greater than 0")
		Integer quantity,

		@Size(max = 255, message = "Reason must not exceed 255 characters")
		String reason,

		Instant movementDate
) {
}
