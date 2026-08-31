package com.stockflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
		@NotBlank(message = "Supplier name is required")
		@Size(max = 100, message = "Supplier name must not exceed 100 characters")
		String name,

		@NotBlank(message = "Contact name is required")
		@Size(max = 150, message = "Contact name must not exceed 150 characters")
		String contactName,

		@NotBlank(message = "Email is required")
		@Email(message = "Email format is invalid")
		@Size(max = 150, message = "Email must not exceed 150 characters")
		String email,

		@Size(max = 30, message = "Phone must not exceed 30 characters")
		String phone,

		@Size(max = 255, message = "Address must not exceed 255 characters")
		String address
) {
}
