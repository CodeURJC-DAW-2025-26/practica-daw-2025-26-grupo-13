package es.codeurjc.daw.library.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Marble {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@OneToOne
	private Image image;

	@ManyToMany
	private List<Shop> shops;

	public Marble() {
	}

	public Marble(String nombre, String description, List<Shop> shops) {
		super();
		this.name = nombre;
		this.description = description;
		this.shops = shops;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public List<Shop> getShops() {
		return shops;
	}

	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}

	@Override
	public String toString() {
		return "Marble [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
