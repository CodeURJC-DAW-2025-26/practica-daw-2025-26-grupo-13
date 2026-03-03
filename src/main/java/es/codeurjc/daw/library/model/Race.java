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

	@ManyToMany
	private List<User> results; //ADD METHODS THAT CALCULATE RESULTS

    @ManyToMany
    private List<User> users;

	public Race() {
		this.users = new ArrayList<User>(8);
		this.results = new ArrayList<User>(8);
	}

	public Race(String name) {
		this.name = name;
		this.users = new ArrayList<User>(8);
		this.results = new ArrayList<User>(8);
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
		if (this.users.size() >= 8) {
			throw new IllegalStateException("Race already has 8 users");
		}
		this.users.add(user);
		if (this.users.size() == 8) {
			calculateResults();
		}
	}

    public void rmvUser(User user) {
		this.users.remove(user);
	}

	public List<User> getResults() {
		return results;
	}

	public List<User> calculateResults() {
		results = new ArrayList<>(users);
		Collections.shuffle(results);
		
		return results;
	}

	public List<User> getTopThreeResults() {
		if (this.results == null) {
			return Collections.emptyList();
		}
		return this.results.stream()
			.limit(3)
			.collect(Collectors.toList());
	}

}