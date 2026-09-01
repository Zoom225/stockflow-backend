package com.stockflow;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockflow.dto.request.LoginRequest;
import com.stockflow.dto.request.UpdateUserRoleRequest;
import com.stockflow.dto.response.AuthResponse;
import com.stockflow.dto.response.ProductResponse;
import com.stockflow.dto.response.UserResponse;
import com.stockflow.entity.UserRole;
import com.stockflow.security.CustomUserDetailsService;
import com.stockflow.service.AuthService;
import com.stockflow.service.CategoryService;
import com.stockflow.service.DashboardService;
import com.stockflow.service.ProductService;
import com.stockflow.service.StockMovementService;
import com.stockflow.service.SupplierService;
import com.stockflow.service.UserService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration," +
				"org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration," +
				"org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration," +
				"org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration",
		"jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWYwMTIzNDU2Nzg5YWJjZGVm",
		"jwt.expiration-ms=86400000"
})
class ApiIntegrationTests {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private ProductService productService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private CategoryService categoryService;

	@MockitoBean
	private SupplierService supplierService;

	@MockitoBean
	private StockMovementService stockMovementService;

	@MockitoBean
	private DashboardService dashboardService;

	@MockitoBean
	private CustomUserDetailsService customUserDetailsService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();
	}

	@Test
	void shouldAllowLoginWithoutAuthentication() throws Exception {
		LoginRequest request = new LoginRequest("jean@example.com", "Password123");
		AuthResponse response = new AuthResponse(
				"jwt-token",
				"Bearer",
				1L,
				"Jean Dupont",
				"jean@example.com",
				"ROLE_ADMIN"
		);

		when(authService.login(request)).thenReturn(response);

		mockMvc.perform(post("/api/auth/login")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").value("jwt-token"))
				.andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
	}

	@Test
	void shouldRejectProtectedProductEndpointWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/products"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "jean@example.com", roles = "USER")
	void shouldAllowAuthenticatedUserToReadProducts() throws Exception {
		when(productService.getAllProducts()).thenReturn(List.of(new ProductResponse(
				1L,
				"SKU-001",
				"Olive Oil",
				"Huile d'olive",
				new java.math.BigDecimal("10.00"),
				new java.math.BigDecimal("15.00"),
				20,
				5,
				false,
				1L,
				"Food",
				null,
				null,
				Instant.parse("2026-09-01T10:00:00Z"),
				Instant.parse("2026-09-01T10:00:00Z")
		)));

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].sku").value("SKU-001"));
	}

	@Test
	@WithMockUser(username = "jean@example.com", roles = "USER")
	void shouldRejectUserManagementForStandardUser() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin@example.com", roles = "ADMIN")
	void shouldAllowAdminToReadUsers() throws Exception {
		when(userService.getAllUsers()).thenReturn(List.of(new UserResponse(
				1L,
				"Admin User",
				"admin@example.com",
				"ROLE_ADMIN",
				Instant.parse("2026-09-01T10:00:00Z"),
				Instant.parse("2026-09-01T10:00:00Z")
		)));

		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email").value("admin@example.com"));
	}

	@Test
	@WithMockUser(username = "admin@example.com", roles = "ADMIN")
	void shouldAllowAdminToUpdateUserRole() throws Exception {
		UpdateUserRoleRequest request = new UpdateUserRoleRequest(UserRole.ROLE_ADMIN);
		UserResponse response = new UserResponse(
				2L,
				"Marie Martin",
				"marie@example.com",
				"ROLE_ADMIN",
				Instant.parse("2026-09-01T10:00:00Z"),
				Instant.parse("2026-09-01T10:00:00Z")
		);

		when(userService.updateUserRole(2L, request)).thenReturn(response);

		mockMvc.perform(put("/api/users/2/role")
						.with(csrf())
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
	}
}
