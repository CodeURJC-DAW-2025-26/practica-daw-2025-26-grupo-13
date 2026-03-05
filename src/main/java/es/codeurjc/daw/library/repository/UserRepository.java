package es.codeurjc.daw.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

	boolean existsByName(String name);

    public List<User> findByRolesContaining(String role);

}