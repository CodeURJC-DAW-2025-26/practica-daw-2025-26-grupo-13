package es.codeurjc.daw.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Race;

public interface RaceRepository extends JpaRepository<Race, Long> {

}