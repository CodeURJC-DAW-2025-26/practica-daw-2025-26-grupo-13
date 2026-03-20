package es.codeurjc.daw.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.daw.library.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

	boolean existsByName(String name);

    public List<User> findByRolesContaining(String role);

    // This query retrieves the top 10 users ordered by their win counter in descending order
    @Query("SELECT u FROM UserTable u ORDER BY u.winCounter DESC")
    public List<User> findTop10ByWinCounterDesc();

}