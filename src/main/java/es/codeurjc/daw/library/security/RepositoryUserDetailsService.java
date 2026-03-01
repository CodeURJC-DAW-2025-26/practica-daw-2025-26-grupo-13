package es.codeurjc.daw.library.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<GrantedAuthority> roles = new ArrayList<>();
		if (user.getRoles() != null) {
			for (String role : user.getRoles()) {
				if (role == null || role.isBlank()) {
					continue;
				}

				String normalizedRole = role.trim().toUpperCase();
				if (!normalizedRole.startsWith("ROLE_")) {
					normalizedRole = "ROLE_" + normalizedRole;
				}

				roles.add(new SimpleGrantedAuthority(normalizedRole));
			}
		}

		if (roles.isEmpty()) {
			roles.add(new SimpleGrantedAuthority("ROLE_USER"));
		}

		return new org.springframework.security.core.userdetails.User(user.getName(), 
				user.getEncodedPassword(), roles);

	}
}
