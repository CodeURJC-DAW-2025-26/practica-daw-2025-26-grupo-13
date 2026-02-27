package es.codeurjc.daw.library.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;

@Entity
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;
	
	private String text;
	
	private int rating;

    @ManyToOne
    private League league;

    @ManyToOne
    private User user;


	public Comment() {}

	public Comment(String text, int rating, User user, League league) {
		this.text = text;
		this.rating = rating;
        this.user = user;
        this.league = league;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void settext(String text) {
		this.text = text;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}


	public User getUser() {
		return user;
	}


	public League getLeague() {
		return league;
	}
}