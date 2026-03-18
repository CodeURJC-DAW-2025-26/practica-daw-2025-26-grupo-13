package es.codeurjc.daw.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.daw.library.model.League;

public interface LeagueRepository extends JpaRepository<League, Long> {

    Optional<League> findByName(String name);

	Optional<League> findByRaces_Id(Long raceId);

    @Query("SELECT l FROM League l LEFT JOIN l.comments c GROUP BY l.id ORDER BY COUNT(c) DESC")
    List<League> findAllOrderByCommentsCountDesc();
}
