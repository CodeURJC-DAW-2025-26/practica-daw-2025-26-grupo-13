package es.codeurjc.daw.library.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

@Entity(name = "UserTable")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String encodedPassword;

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
		this.encodedPassword = encodedPassword;
		this.roles = List.of(roles);
		this.comments = new ArrayList<>();
		this.marbles = new ArrayList<>(3);
		this.winCounter = 1;
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

	public List<Marble> getMarbles() {
		return marbles;
	}

	public void setMarble(Marble marble, int position) {
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