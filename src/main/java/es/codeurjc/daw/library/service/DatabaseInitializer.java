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

    private final UserService userService;

	@Autowired
	private MarbleService marbleService;

	@Autowired
	private LeagueService leagueService;

	@Autowired
	private RaceService raceService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

    DatabaseInitializer(UserService userService) {
        this.userService = userService;
    }

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

		List<User> usersList = List.of(u1, u2, u3, u4, u5, u6, u7, u8, u9);

		userService.save(u1);
		userService.save(u2);
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

		// Create 3 leagues
		League l1 = new League("Liga A");
		League l2 = new League("Liga B");
		League l3 = new League("Liga C");

		// Create races
		Race r1 = new Race("Carrera Frirum");
		Race r2 = new Race("Carrera Neger");
		Race r3 = new Race("Carrera Co'uhm");
		Race r4 = new Race("Carrera Peeka");
		Race r5 = new Race("Carrera Chick'uhm");
		Race r6 = new Race("Carrera Lil Wiggin on da Beat");

		for (int i = 0; i < 8; i++) {
			r1.addUser(usersList.get(i));
			r2.addUser(usersList.get(i));
			r3.addUser(usersList.get(i));
			r4.addUser(usersList.get(i));
			r5.addUser(usersList.get(i));
			r6.addUser(usersList.get(i));
		} 

		l1.setRace(0, r1);
		l1.setRace(1, r2);
		l1.setRace(2, r3);
		l1.setRace(3, r4);
		l1.setRace(4, r5);
		l1.setRace(5, r6);

		leagueService.save(l1);
		leagueService.save(l2);
		leagueService.save(l3);
	}
}
