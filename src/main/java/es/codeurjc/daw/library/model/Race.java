package es.codeurjc.daw.library.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Race {

	private static final int MAX_USERS = 8;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@ManyToMany(fetch = FetchType.EAGER) // we need to fetch the users eagerly to show their names
	@jakarta.persistence.OrderColumn(name = "result_order")
	private List<User> results;

	@ManyToMany(fetch = FetchType.EAGER)
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
		// We check if the user and the state of the race before adding the user to the race
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		if (isFinished()) {
			throw new IllegalStateException("Race already finished");
		}
		if (this.users == null) {
			this.users = new ArrayList<User>(MAX_USERS);
		}
		if (this.users.size() >= MAX_USERS) {
			throw new IllegalStateException("Race already has " + MAX_USERS + " users");
		}
		long userId = user.getId();
		boolean alreadyInRace = this.users.stream().anyMatch(u -> u.getId() == userId);
		if (alreadyInRace) {
			return;
		}
		this.users.add(user);
	}

    public void rmvUser(User user) {
		this.users.remove(user);
	}

	public List<User> getResults() {
		return results;
	}

	public List<User> calculateResults() {
		// If the race is finished, return the results, otherwise calculate the results by shuffling the users and return them
		if (isFinished()) {
			return this.results;
		}
		if (this.users == null || this.users.isEmpty()) {
			return Collections.emptyList();
		}
		List<User> shuffled = new ArrayList<>(users);
		Collections.shuffle(shuffled);
		
		this.results.clear();
		this.results.addAll(shuffled);
		
		return this.results;
	}

	public boolean isFinished() {
		return this.results != null && !this.results.isEmpty();
	}

	public String getWinnerName() {
		return isFinished() ? this.results.get(0).getName() : "Sin ganador";
	}

	public List<User> getTopThreeResults() {
		//if there are no results, return an empty list, otherwise return the top 3 results
		if (this.results == null) {
			return Collections.emptyList();
		}
		return this.results.stream()
			.limit(3)
			.collect(Collectors.toList());
	}

}