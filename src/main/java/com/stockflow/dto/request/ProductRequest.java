package com.stockflow.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
		@NotBlank(message = "Le SKU du produit est obligatoire")
		@Size(max = 50, message = "Le SKU du produit ne doit pas depasser 50 caracteres")
		String sku,

		@NotBlank(message = "Le nom du produit est obligatoire")
		@Size(max = 150, message = "Le nom du produit ne doit pas depasser 150 caracteres")
		String name,

		@Size(max = 255, message = "La description du produit ne doit pas depasser 255 caracteres")
		String description,

		@NotNull(message = "Le prix d'achat est obligatoire")
		@DecimalMin(value = "0.00", inclusive = true, message = "Le prix d'achat doit etre superieur ou egal a 0")
		BigDecimal purchasePrice,

		@NotNull(message = "Le prix de vente est obligatoire")
		@DecimalMin(value = "0.00", inclusive = true, message = "Le prix de vente doit etre superieur ou egal a 0")
		BigDecimal sellingPrice,

		@NotNull(message = "L'identifiant de la categorie est obligatoire")
		Long categoryId,

		@NotNull(message = "Le stock minimum est obligatoire")
		@Min(value = 0, message = "Le stock minimum doit etre superieur ou egal a 0")
		Integer minimumStock,

		Long supplierId
) {
}
