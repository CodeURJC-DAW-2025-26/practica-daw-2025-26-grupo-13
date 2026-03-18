package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.League;
import es.codeurjc.daw.library.repository.LeagueRepository;

@Service
public class LeagueService {

	@Autowired
	private LeagueRepository repository;

	public Optional<League> findById(long id) {
		return repository.findById(id);
	}

	public List<League> findAllById(List<Long> ids){
		return repository.findAllById(ids);
	}

	public List<League> findFiltered(){
		return repository.findAllOrderByCommentsCountDesc();
	}


	public Page<League> findAll(Pageable pageable) {
    return repository.findAll(pageable);
}
	
	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<League> findAll() {
		return repository.findAll();
	}

	public void save(League league) {
		repository.save(league);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	
}