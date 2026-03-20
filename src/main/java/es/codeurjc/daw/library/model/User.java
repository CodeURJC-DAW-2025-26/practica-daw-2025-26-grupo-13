package es.codeurjc.daw.library.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity(name = "UserTable")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String encodedPassword;
	@Column(nullable = false)
	private String email;

	private int winCounter;
	private int loseCounter;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

	@OneToOne
	private Image image;

	@OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Marble> marbles;
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;

	public User() {
	}

	public User(String name, String encodedPassword, String... roles) {
		this.name = name;
		this.image = null;
		this.email = null;
		this.encodedPassword = encodedPassword;
		this.roles = List.of(roles);
		this.comments = new ArrayList<>();
		this.marbles = new ArrayList<>(3);
		this.winCounter = 0;
		this.loseCounter = 0;
	}

	public long getId() {
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

	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

	public String getEmail() {
		return email;
	}

	public int getWinCounter() {
		return winCounter;
	}

	public void incrementWinCounter() {
		this.winCounter++;
	}

	public int getLoseCounter() {
		return loseCounter;
	}

	public void incrementLoseCounter() {
		this.loseCounter++;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Marble> getMarbles() {
		return marbles;
	}

	public void setMarble(Marble marble, int position) {
		// We check if the user already has 3 marbles, if it does we throw an exception, otherwise we add the marble to the user's marbles
		if (this.marbles.size() >= 3) {
			throw new IllegalStateException("User already has 3 marbles");
		}
		this.marbles.add(position, marble);
	}
	
	public void rmvMarble(Marble marble) {
		this.marbles.remove(marble);
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
	}

    public void rmvComment(Comment comment) {
		this.comments.remove(comment);
	}
}