package com.stockflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockflow.dto.request.LoginRequest;
import com.stockflow.entity.AppUser;
import com.stockflow.entity.UserRole;
import com.stockflow.repository.UserRepository;
import com.stockflow.security.CustomUserDetailsService;
import com.stockflow.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(properties = {
		"spring.flyway.enabled=true",
		"spring.jpa.hibernate.ddl-auto=validate",
		"jwt.expiration-ms=86400000"
})
class DemoAccountLoginIT {

	private static final String DEMO_EMAIL = "demo@stockflow.app";
	private static final String DEMO_PASSWORD = "DemoStock2026!";

	@Container
	static final PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtService jwtService;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private MockMvc mockMvc;

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("jwt.secret", DemoAccountLoginIT::testJwtSecret);
	}

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();
	}

	@Test
	void shouldAuthenticateTheFlywayDemoAccountAndReturnAValidJwt() throws Exception {
		AppUser demoUser = userRepository.findByEmailIgnoreCase(DEMO_EMAIL).orElseThrow();
		assertThat(userRepository.count()).isEqualTo(1);
		assertThat(demoUser.getFullName()).isEqualTo("Démonstration StockFlow");
		assertThat(demoUser.getRole()).isEqualTo(UserRole.ROLE_USER);
		assertThat(demoUser.getPasswordHash()).isNotEqualTo(DEMO_PASSWORD).startsWith("$2a$10$");
		assertThat(passwordEncoder.matches(DEMO_PASSWORD, demoUser.getPasswordHash())).isTrue();

		LoginRequest request = new LoginRequest(DEMO_EMAIL, DEMO_PASSWORD);
		MvcResult result = mockMvc.perform(post("/api/auth/login")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.fullName").value("Démonstration StockFlow"))
				.andExpect(jsonPath("$.email").value(DEMO_EMAIL))
				.andExpect(jsonPath("$.role").value("ROLE_USER"))
				.andReturn();

		JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
		String accessToken = responseBody.path("accessToken").asText();
		UserDetails userDetails = userDetailsService.loadUserByUsername(DEMO_EMAIL);

		assertThat(accessToken).isNotBlank();
		assertThat(jwtService.extractUsername(accessToken)).isEqualTo(DEMO_EMAIL);
		assertThat(jwtService.isTokenValid(accessToken, userDetails)).isTrue();
	}

	private static String testJwtSecret() {
		return Base64.getEncoder()
				.encodeToString("stockflow-demo-test-jwt-signing-key-not-for-real-use"
						.getBytes(StandardCharsets.UTF_8));
	}
}
