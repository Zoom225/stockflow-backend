package com.stockflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockflow.dto.request.UpdateUserRequest;
import com.stockflow.dto.request.UpdateUserRoleRequest;
import com.stockflow.dto.response.UserResponse;
import com.stockflow.entity.AppUser;
import com.stockflow.entity.UserRole;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.UserMapper;
import com.stockflow.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

	@Mock
	private UserRepository userRepository;

	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(userRepository, new UserMapper());
	}

	@Test
	void shouldReturnAllUsers() {
		AppUser first = buildUser(1L, "Jean Dupont", "jean@example.com");
		AppUser second = buildUser(2L, "Marie Martin", "marie@example.com");

		when(userRepository.findAll()).thenReturn(List.of(first, second));

		List<UserResponse> responses = userService.getAllUsers();

		assertEquals(2, responses.size());
		assertEquals("Jean Dupont", responses.getFirst().fullName());
		assertEquals("marie@example.com", responses.get(1).email());
	}

	@Test
	void shouldReturnUserById() {
		AppUser user = buildUser(1L, "Jean Dupont", "jean@example.com");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		UserResponse response = userService.getUserById(1L);

		assertEquals(1L, response.id());
		assertEquals("Jean Dupont", response.fullName());
	}

	@Test
	void shouldThrowWhenUserNotFound() {
		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
	}

	@Test
	void shouldUpdateUser() {
		AppUser existingUser = buildUser(1L, "Jean Dupont", "jean@example.com");
		AppUser updatedUser = buildUser(1L, "Jean Michel Dupont", "jean.michel@example.com");
		UpdateUserRequest request = new UpdateUserRequest("Jean Michel Dupont", "jean.michel@example.com");

		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(userRepository.findByEmailIgnoreCase("jean.michel@example.com")).thenReturn(Optional.empty());
		when(userRepository.save(existingUser)).thenReturn(updatedUser);

		UserResponse response = userService.updateUser(1L, request);

		assertEquals("Jean Michel Dupont", response.fullName());
		assertEquals("jean.michel@example.com", response.email());
	}

	@Test
	void shouldRejectDuplicateEmailOnUpdate() {
		AppUser existingUser = buildUser(1L, "Jean Dupont", "jean@example.com");
		AppUser duplicateUser = buildUser(2L, "Marie Martin", "marie@example.com");
		UpdateUserRequest request = new UpdateUserRequest("Jean Dupont", "marie@example.com");

		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(userRepository.findByEmailIgnoreCase("marie@example.com")).thenReturn(Optional.of(duplicateUser));

		assertThrows(DuplicateResourceException.class, () -> userService.updateUser(1L, request));
		verify(userRepository, never()).save(existingUser);
	}

	@Test
	void shouldDeleteUser() {
		AppUser user = buildUser(1L, "Jean Dupont", "jean@example.com");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		userService.deleteUser(1L);

		verify(userRepository).delete(user);
	}

	@Test
	void shouldUpdateUserRole() {
		AppUser existingUser = buildUser(1L, "Jean Dupont", "jean@example.com");
		AppUser updatedUser = buildUser(1L, "Jean Dupont", "jean@example.com");
		updatedUser.setRole(UserRole.ROLE_ADMIN);

		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(existingUser)).thenReturn(updatedUser);

		UserResponse response = userService.updateUserRole(1L, new UpdateUserRoleRequest(UserRole.ROLE_ADMIN));

		assertEquals("ROLE_ADMIN", response.role());
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
