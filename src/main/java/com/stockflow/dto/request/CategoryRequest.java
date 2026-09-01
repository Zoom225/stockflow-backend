package com.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
		@NotBlank(message = "Le nom de la categorie est obligatoire")
		@Size(max = 100, message = "Le nom de la categorie ne doit pas depasser 100 caracteres")
		String name,

		@Size(max = 255, message = "La description de la categorie ne doit pas depasser 255 caracteres")
		String description
) {
}
