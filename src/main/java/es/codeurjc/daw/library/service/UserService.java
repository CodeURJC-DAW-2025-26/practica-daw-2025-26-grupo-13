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

	public void delete(long id) {
		userRepository.deleteById(id);
	}
	

	public void save(User user) {
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
		userRepository.save(user);
	}

	public void setUserImage(User user, Image image) throws IOException {
	
		user.setImage(image);
		userRepository.save(user);
	}
}
