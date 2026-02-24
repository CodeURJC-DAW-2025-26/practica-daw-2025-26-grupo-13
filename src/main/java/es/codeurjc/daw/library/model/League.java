package es.codeurjc.daw.library.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class League {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

    private boolean status;

    @OneToMany(cascade=CascadeType.ALL)
	private List<Race> races;

    @OneToMany
    private List<User> users;

    @OneToMany(cascade=CascadeType.ALL)
    private List<Comment> comments;

	public League() {
	}

	public League(String name) {
		this.name = name;
		this.users = new ArrayList<>();
		this.races = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.status = true;
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

	public List<Race> getRaces() {
		return races;
	}

	public void addRace(Race race) {
		this.races.add(race);
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

    public List<Comment> getComments() {
		return comments;
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
	}

    public void rmvUser(Comment comment) {
		this.comments.remove(comment);
	}


}