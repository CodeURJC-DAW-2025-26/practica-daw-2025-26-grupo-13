package es.codeurjc.daw.library.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

@Entity
public class League {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

    private boolean status;

    private int race_num;

    @OneToMany(cascade=CascadeType.ALL)
	private List<Race> races;

    @OneToMany
    private List<User> users;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<Comment> comments;

	public League() {
	}

	public League(String name) {
		super();
		this.name = name;
		this.races = new ArrayList<Race>();
		for (int i = 1; i <= 6; i++) {
			this.races.add(new Race("Carrera " + i));
		}
        this.comments = new ArrayList<>();
        this.status = true;
		this.race_num = 6;
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

	public int getRace_num() {
		return race_num;
	}

	public void setRace_num(int race_num) {
		this.race_num = race_num;
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

    public List<Comment> getComments() {
		return comments;
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
	}

    public void rmvComment(Comment comment) {
		this.comments.remove(comment);
	}


}