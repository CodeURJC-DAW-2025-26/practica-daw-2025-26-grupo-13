package es.codeurjc.daw.library.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

    public Optional<User> findById(long id) {
		return userRepository.findById(id);
	}

	public Optional<User> findByName(String name) {
		return userRepository.findByName(name);
	}
	
	public boolean exist(long id) {
		return userRepository.existsById(id);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public boolean existByName(String name) {
		return userRepository.existsByName(name);
	}

	public void delete(long id) {
		userRepository.deleteById(id);
	}
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	public void save(User user) {
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			if (user.getId() > 0) {
				User dbUser = userRepository.findById(user.getId()).orElse(null);
				if (dbUser != null && dbUser.getRoles() != null && !dbUser.getRoles().isEmpty()) {
					user.setRoles(normalizeRoles(dbUser.getRoles()));
				} else {
					user.setRoles(List.of("USER"));
				}
			} else {
				user.setRoles(List.of("USER"));
			}
		} else {
			user.setRoles(normalizeRoles(user.getRoles()));
		}

		if (user.getEncodedPassword() != null && !isEncodedPassword(user.getEncodedPassword())) {
			user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
		}
		userRepository.save(user);
	}

	public User registerUser(User user) {
		user.setRoles(List.of("USER"));
		user.setRoles(normalizeRoles(user.getRoles()));
		user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
		return userRepository.save(user);
	}

	public void setUserImage(User user, Image image) throws IOException {
		user.setImage(image);
	}

	private boolean isEncodedPassword(String password) {
		return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
	}

	private List<String> normalizeRoles(List<String> roles) {
		if (roles == null || roles.isEmpty()) {
			return List.of("USER");
		}

		List<String> normalizedRoles = roles.stream()
				.filter(role -> role != null && !role.isBlank())
				.map(String::trim)
				.map(String::toUpperCase)
				.map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
				.distinct()
				.toList();

		if (normalizedRoles.isEmpty()) {
			return List.of("USER");
		}

		return normalizedRoles;
	}
	public List<User> findOnlyUsers() {
		return userRepository.findByRolesContaining("USER");
	}
}