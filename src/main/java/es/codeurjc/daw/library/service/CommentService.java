package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository repository;

	public Optional<Comment> findById(long id) {
		return repository.findById(id);
	}
	
	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<Comment> findAll() {
		return repository.findAll();
	}

	public void save(Comment comment) {
		repository.save(comment);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}
}