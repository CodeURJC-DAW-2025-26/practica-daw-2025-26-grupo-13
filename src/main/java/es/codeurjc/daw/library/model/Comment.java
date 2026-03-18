package es.codeurjc.daw.library.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;
	
	private String text;
	
	private int rating;

    @ManyToOne
	@JsonIgnore
    private League league;

    @ManyToOne
    private User user;

	@Transient
    private boolean editAllowed;

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

	public boolean isEditAllowed() {
        return editAllowed;
    }

    public void setEditAllowed(boolean editAllowed) {
        this.editAllowed = editAllowed;
    }
}