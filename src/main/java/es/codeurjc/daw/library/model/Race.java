package es.codeurjc.daw.library.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Race {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private List<User> results; //ADD METHODS THAT CALCULATE RESULTS

    @ManyToMany
    private List<User> users;

	public Race() {
	}

	public Race(String name) {
		this.name = name;
		this.users = new ArrayList<>();
		this.results = new ArrayList<>();
	}

    public Long getId() {
		return id;
	}

    public void setId(long id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void addUser(User user) {
		this.users.add(user);
	}

    public void rmvUser(User user) {
		this.users.remove(user);
	}

	public List<User> calculateResults(List<User> results) {
		// if you only want this for races with
		// exactly eight participants you can add a size check here.
		if (results != null) {
			Collections.shuffle(results);
		}
		return results;
	}

	/**
	 * Helper for templates: returns at most three users from the current results.
	 */
	public List<User> getTopThreeResults() {
		if (this.results == null) {
			return Collections.emptyList();
		}
		return this.results.stream()
			.limit(3)
			.collect(Collectors.toList());
	}

}