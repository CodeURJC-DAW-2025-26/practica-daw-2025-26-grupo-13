package es.codeurjc.daw.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.League;

public interface LeagueRepository extends JpaRepository<League, Long> {

    Optional<League> findByName(String name);

}
