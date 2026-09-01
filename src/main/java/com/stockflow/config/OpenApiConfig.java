package com.stockflow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI stockflowOpenApi() {
		String bearerSchemeName = "bearerAuth";

		return new OpenAPI()
				.info(new Info()
						.title("StockFlow API")
						.description("Documentation de l'API de gestion de stock StockFlow")
						.version("v1")
						.contact(new Contact().name("StockFlow")))
				.addSecurityItem(new SecurityRequirement().addList(bearerSchemeName))
				.components(new Components()
						.addSecuritySchemes(
								bearerSchemeName,
								new SecurityScheme()
										.name("Authorization")
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
						));
	}
}
