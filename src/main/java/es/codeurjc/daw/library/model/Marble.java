package es.codeurjc.daw.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Marble {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

	private String name;
	private Long userID;
	private boolean isChosen;

	@OneToOne
	private Image image;

	public Marble() {
	}

	public Marble(String name, Image image, Long userID) {
		super();
		this.name = name; //field not used in races yet, will be used when we can show race progress
		this.image = image;
		this.userID = userID;
		this.isChosen = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getUser() {
		return userID;
	}

	public void setUser(Long userId) {
		this.userID = userId;
	}

	public boolean isChosen() {
		return isChosen;
	}

	public void setChosen(boolean isChosen) {
		this.isChosen = isChosen;
	}

	@Override
	public String toString() {
		return "Marble [id=" + id + ", name=" + name + "]"; 
	}
}
