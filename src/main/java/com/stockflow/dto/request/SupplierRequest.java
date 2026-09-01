package com.stockflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
		@NotBlank(message = "Le nom du fournisseur est obligatoire")
		@Size(max = 100, message = "Le nom du fournisseur ne doit pas depasser 100 caracteres")
		String name,

		@NotBlank(message = "Le nom du contact est obligatoire")
		@Size(max = 150, message = "Le nom du contact ne doit pas depasser 150 caracteres")
		String contactName,

		@NotBlank(message = "L'email est obligatoire")
		@Email(message = "Le format de l'email est invalide")
		@Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
		String email,

		@Size(max = 30, message = "Le numero de telephone ne doit pas depasser 30 caracteres")
		String phone,

		@Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres")
		String address
) {
}
