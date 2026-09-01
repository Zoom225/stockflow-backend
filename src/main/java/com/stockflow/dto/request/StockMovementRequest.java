package com.stockflow.dto.request;

import com.stockflow.entity.StockMovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record StockMovementRequest(
		@NotNull(message = "L'identifiant du produit est obligatoire")
		Long productId,

		@NotNull(message = "Le type de mouvement est obligatoire")
		StockMovementType type,

		@NotNull(message = "La quantite est obligatoire")
		@Min(value = 1, message = "La quantite doit etre superieure a 0")
		Integer quantity,

		@Size(max = 255, message = "Le motif ne doit pas depasser 255 caracteres")
		String reason,

		Instant movementDate
) {
}
