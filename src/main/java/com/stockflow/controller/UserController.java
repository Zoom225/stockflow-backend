package com.stockflow.controller;

import com.stockflow.dto.request.UpdateUserRequest;
import com.stockflow.dto.request.UpdateUserRoleRequest;
import com.stockflow.dto.response.UserResponse;
import com.stockflow.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(
			@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequest request
	) {
		return ResponseEntity.ok(userService.updateUser(id, request));
	}

	@PutMapping("/{id}/role")
	public ResponseEntity<UserResponse> updateUserRole(
			@PathVariable Long id,
			@Valid @RequestBody UpdateUserRoleRequest request
	) {
		return ResponseEntity.ok(userService.updateUserRole(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
