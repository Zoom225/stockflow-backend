package com.stockflow.security;

import com.stockflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmailIgnoreCase(username)
				.map(user -> User.withUsername(user.getEmail())
						.password(user.getPasswordHash())
						.authorities(new SimpleGrantedAuthority(user.getRole().name()))
						.build())
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + username));
	}
}
