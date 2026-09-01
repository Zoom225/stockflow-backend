package com.stockflow.dto.request;

import com.stockflow.entity.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
		@NotNull(message = "Le role est obligatoire")
		UserRole role
) {
}
