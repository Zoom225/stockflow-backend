package com.stockflow.service.impl;

import com.stockflow.dto.request.UpdateUserRequest;
import com.stockflow.dto.request.UpdateUserRoleRequest;
import com.stockflow.dto.response.UserResponse;
import com.stockflow.entity.AppUser;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.UserMapper;
import com.stockflow.repository.UserRepository;
import com.stockflow.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream()
				.map(userMapper::toResponse)
				.toList();
	}

	@Override
	public UserResponse getUserById(Long id) {
		return userMapper.toResponse(findUserById(id));
	}

	@Override
	@Transactional
	public UserResponse updateUser(Long id, UpdateUserRequest request) {
		AppUser user = findUserById(id);
		validateUniqueEmail(request.email(), id);

		userMapper.updateEntity(user, request);
		AppUser updatedUser = userRepository.save(user);
		return userMapper.toResponse(updatedUser);
	}

	@Override
	@Transactional
	public UserResponse updateUserRole(Long id, UpdateUserRoleRequest request) {
		AppUser user = findUserById(id);

		// Regle metier : seul un changement de role explicite doit modifier les droits d'un utilisateur.
		user.setRole(request.role());
		AppUser updatedUser = userRepository.save(user);
		return userMapper.toResponse(updatedUser);
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		AppUser user = findUserById(id);
		userRepository.delete(user);
	}

	private AppUser findUserById(Long id) {
		// Regle metier : un utilisateur doit exister avant toute consultation, modification ou suppression.
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'identifiant : " + id));
	}

	private void validateUniqueEmail(String email, Long userId) {
		String normalizedEmail = email == null ? null : email.trim().toLowerCase();

		// Regle metier : l'email d'un utilisateur doit rester unique dans le systeme.
		boolean exists = userRepository.findByEmailIgnoreCase(normalizedEmail)
				.filter(existingUser -> !existingUser.getId().equals(userId))
				.isPresent();

		if (exists) {
			throw new DuplicateResourceException("Un utilisateur existe deja avec cet email : " + normalizedEmail);
		}
	}
}
