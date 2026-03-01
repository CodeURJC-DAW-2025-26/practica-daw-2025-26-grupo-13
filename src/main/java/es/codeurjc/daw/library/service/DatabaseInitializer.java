package es.codeurjc.daw.library.service;

import java.io.IOException;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Marble;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.League;
import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class DatabaseInitializer {

	@Autowired
	private MarbleService marbleService;

	@Autowired
	private LeagueService leagueService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	public void init() throws IOException {

		// Create two users
		User u1 = new User("manolo", passwordEncoder.encode("qwer"), "USER");
		User u2 = new User("pepe", passwordEncoder.encode("1234"), "ADMIN");

		userRepository.save(u1);
		userRepository.save(u2);

		// Create one marble for each user (no image)
		Marble m1 = new Marble("Canica Manolo", null, u1.getId());
		Marble m2 = new Marble("Canica Pepe", null, u2.getId());

		marbleService.save(m1);
		marbleService.save(m2);

		// Create two leagues
		League l1 = new League("Liga A");
		League l2 = new League("Liga B");

		leagueService.save(l1);
		leagueService.save(l2);
	}
}
