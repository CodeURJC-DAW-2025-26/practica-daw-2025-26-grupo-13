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
	

	public void save(User user) {
		if (user.getEncodedPassword() != null && !isEncodedPassword(user.getEncodedPassword())) {
			user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
		}
		userRepository.save(user);
	}

	public User registerUser(User user) {
		user.setRoles(List.of("USER"));
		user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
		return userRepository.save(user);
	}

	public void setUserImage(User user, Image image) throws IOException {
		user.setImage(image);
	}

	private boolean isEncodedPassword(String password) {
		return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
	}
}
