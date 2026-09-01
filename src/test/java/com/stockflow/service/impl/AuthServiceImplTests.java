package com.stockflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockflow.dto.request.LoginRequest;
import com.stockflow.dto.request.RegisterRequest;
import com.stockflow.dto.response.AuthResponse;
import com.stockflow.entity.AppUser;
import com.stockflow.entity.UserRole;
import com.stockflow.exception.AuthenticationFailedException;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.repository.UserRepository;
import com.stockflow.security.CustomUserDetailsService;
import com.stockflow.security.JwtService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private CustomUserDetailsService userDetailsService;

	private AuthServiceImpl authService;

	@BeforeEach
	void setUp() {
		authService = new AuthServiceImpl(
				userRepository,
				passwordEncoder,
				jwtService,
				authenticationManager,
				userDetailsService
		);
	}

	@Test
	void shouldRegisterUser() {
		RegisterRequest request = new RegisterRequest("Jean Dupont", "jean@example.com", "Password123");
		AppUser savedUser = buildUser(1L, "Jean Dupont", "jean@example.com");
		User userDetails = new User(
				"jean@example.com",
				"encoded-password",
				List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);

		when(userRepository.existsByEmailIgnoreCase("jean@example.com")).thenReturn(false);
		when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");
		when(userRepository.save(any(AppUser.class))).thenReturn(savedUser);
		when(userDetailsService.loadUserByUsername("jean@example.com")).thenReturn(userDetails);
		when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

		AuthResponse response = authService.register(request);

		assertNotNull(response);
		assertEquals("jwt-token", response.accessToken());
		assertEquals("jean@example.com", response.email());
		verify(userRepository).save(any(AppUser.class));
	}

	@Test
	void shouldRejectDuplicateEmailOnRegister() {
		RegisterRequest request = new RegisterRequest("Jean Dupont", "jean@example.com", "Password123");

		when(userRepository.existsByEmailIgnoreCase("jean@example.com")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> authService.register(request));
	}

	@Test
	void shouldLoginUser() {
		LoginRequest request = new LoginRequest("jean@example.com", "Password123");
		AppUser user = buildUser(1L, "Jean Dupont", "jean@example.com");
		User userDetails = new User(
				"jean@example.com",
				"encoded-password",
				List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);

		when(userRepository.findByEmailIgnoreCase("jean@example.com")).thenReturn(Optional.of(user));
		when(userDetailsService.loadUserByUsername("jean@example.com")).thenReturn(userDetails);
		when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

		AuthResponse response = authService.login(request);

		assertEquals("jwt-token", response.accessToken());
		verify(authenticationManager).authenticate(
				new UsernamePasswordAuthenticationToken("jean@example.com", "Password123")
		);
	}

	@Test
	void shouldRejectInvalidCredentialsOnLogin() {
		LoginRequest request = new LoginRequest("jean@example.com", "wrong-password");

		when(authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("jean@example.com", "wrong-password")
		)).thenThrow(new BadCredentialsException("bad credentials"));

		assertThrows(AuthenticationFailedException.class, () -> authService.login(request));
	}

	private AppUser buildUser(Long id, String fullName, String email) {
		AppUser user = new AppUser();
		user.setId(id);
		user.setFullName(fullName);
		user.setEmail(email);
		user.setPasswordHash("encoded-password");
		user.setRole(UserRole.ROLE_USER);
		user.setCreatedAt(Instant.parse("2026-09-01T10:00:00Z"));
		user.setUpdatedAt(Instant.parse("2026-09-01T10:00:00Z"));
		return user;
	}
}
