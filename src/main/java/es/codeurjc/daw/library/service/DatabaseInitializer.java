package es.codeurjc.daw.library.service;

import java.io.IOException;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Marble;
import es.codeurjc.daw.library.model.Race;
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

		// Create eight normal users and one admin user
		User u1 = new User("manolo", passwordEncoder.encode("qwer"), "USER");
		User u2 = new User("jora", passwordEncoder.encode("1234"), "USER");
		User u3 = new User("juan", passwordEncoder.encode("5678"), "USER");
		User u4 = new User("ana", passwordEncoder.encode("abcd"), "USER");
		User u5 = new User("luis", passwordEncoder.encode("efgh"), "USER");
		User u6 = new User("maria", passwordEncoder.encode("ijkl"), "USER");
		User u7 = new User("carlos", passwordEncoder.encode("mnop"), "USER");
		User u8 = new User("sara", passwordEncoder.encode("qrst"), "USER");
		User u9 = new User("pepe", passwordEncoder.encode("1234"), "ADMIN");

		userRepository.save(u1);
		userRepository.save(u2);
		userRepository.save(u3);
		userRepository.save(u4);
		userRepository.save(u5);
		userRepository.save(u6);
		userRepository.save(u7);
		userRepository.save(u8);
		userRepository.save(u9);

		// Create one marble for each user (no image)
		Marble m1 = new Marble("Canica Manolo", null, u1.getId());
		Marble m2 = new Marble("Canica Pepe", null, u2.getId());

		marbleService.save(m1);
		marbleService.save(m2);

		// Create two leagues
		League l1 = new League("Liga A");
		League l2 = new League("Liga B");
		League l3 = new League("Liga C");

		leagueService.save(l1);
		leagueService.save(l2);
		leagueService.save(l3);

	/*	List<Race> races = l1.getRaces();

		for (int i = 0; i < 8; i++) {
			races.get(i).addUser(u1);
		} */
	}
}
