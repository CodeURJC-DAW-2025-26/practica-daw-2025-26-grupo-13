package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Race;
import es.codeurjc.daw.library.repository.RaceRepository;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.model.User;

@Service
public class RaceService {

	@Autowired
	private RaceRepository repository;

	@Autowired
	private UserRepository userRepository;

	public Optional<Race> findById(long id) {
		return repository.findById(id);
	}
	
	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<Race> findAll() {
		return repository.findAll();
	}

	public void save(Race race) {
		repository.save(race);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	public void fillWithUsers(Race race) {
		for (int i = 1; i < 10; i++) {
			race.addUser(userRepository.findById((long) (i)).orElseThrow());
		}
	}
}