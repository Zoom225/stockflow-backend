package com.stockflow.service.impl;

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
import com.stockflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final CustomUserDetailsService userDetailsService;

	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String normalizedEmail = request.email().trim().toLowerCase();

		// Regle metier : un email utilisateur doit etre unique pour eviter les comptes en doublon.
		if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
			throw new DuplicateResourceException("Un utilisateur existe deja avec cet email : " + normalizedEmail);
		}

		AppUser user = new AppUser();
		user.setFullName(request.fullName().trim());
		user.setEmail(normalizedEmail);
		user.setPasswordHash(passwordEncoder.encode(request.password()));

		// Regle metier : le premier compte cree devient administrateur pour permettre l'administration initiale.
		user.setRole(userRepository.count() == 0 ? UserRole.ROLE_ADMIN : UserRole.ROLE_USER);

		AppUser savedUser = userRepository.save(user);
		UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
		String token = jwtService.generateToken(userDetails);

		return new AuthResponse(
				token,
				"Bearer",
				savedUser.getId(),
				savedUser.getFullName(),
				savedUser.getEmail(),
				savedUser.getRole().name()
		);
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		String normalizedEmail = request.email().trim().toLowerCase();

		try {
			// Regle metier : la connexion n'est autorisee qu'avec des identifiants valides.
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
			);
		} catch (BadCredentialsException exception) {
			throw new AuthenticationFailedException("Email ou mot de passe incorrect.");
		} catch (AuthenticationException exception) {
			throw new AuthenticationFailedException("Connexion impossible.");
		}

		AppUser user = userRepository.findByEmailIgnoreCase(normalizedEmail)
				.orElseThrow(() -> new AuthenticationFailedException("Utilisateur introuvable."));

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		String token = jwtService.generateToken(userDetails);

		return new AuthResponse(
				token,
				"Bearer",
				user.getId(),
				user.getFullName(),
				user.getEmail(),
				user.getRole().name()
		);
	}
}
