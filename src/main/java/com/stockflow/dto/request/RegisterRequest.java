package com.stockflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank(message = "Le nom complet est obligatoire")
		@Size(max = 150, message = "Le nom complet ne doit pas depasser 150 caracteres")
		String fullName,

		@NotBlank(message = "L'email est obligatoire")
		@Email(message = "Le format de l'email est invalide")
		@Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
		String email,

		@NotBlank(message = "Le mot de passe est obligatoire")
		@Size(min = 8, max = 100, message = "Le mot de passe doit contenir entre 8 et 100 caracteres")
		String password
) {
}
