package es.codeurjc.daw.library.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;
import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.LeagueService;
import es.codeurjc.daw.library.service.UserService;


@Controller
public class CommentController {

    private final UserRepository userRepository;


	@Autowired
	private CommentService commentService;

    @Autowired
    private LeagueService leagueService;

    @Autowired
    private UserService userService;

    CommentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
			userRepository.findByName(principal.getName()).ifPresent(user -> model.addAttribute("userid", user.getId()));

		} else {
			model.addAttribute("logged", false);
		}
	}

	@GetMapping("/create-comment/{leagueId}")
	public String newComment(Model model, @PathVariable long leagueId) {

		model.addAttribute("leagueId", leagueId);
		return "create-comment";
	}

    @PostMapping("/create-comment")
    public String postCreateComment(Model model, @RequestParam String text, @RequestParam int rating, @RequestParam long leagueId, Principal principal) {

        // create a comment only if we have a logged user and the league exists
        if (principal != null) {
            User user = userService.findByName(principal.getName()).orElse(null);
            leagueService.findById(leagueId).ifPresent(league -> {
                Comment comment = new Comment(text, rating, user, league);
                commentService.save(comment);
            });
        }
        // after creation we redirect to the listing page
        return "redirect:/";
    }
    
    
    @GetMapping("/edit-comment/{id}")
	public String editComment(Model model, @PathVariable long id) {
        Optional<Comment> c = commentService.findById(id);
        if (c.isPresent()) {
            model.addAttribute("comment", c.get());
        }
		return "edit-comment";
	}

     @GetMapping("/edit-comment-admin/{id}")
	public String editCommentAdmin(Model model, @PathVariable long id) {

        Optional<Comment> c = commentService.findById(id);
        if (c.isPresent()) {
            model.addAttribute("comment", c.get());
        }
        // we don't allow to change the league in the form, so no need to pass leagues
        return "edit-comment";
	}

    @PostMapping("/edit-comment")
	public String postEditComment(Model model, @RequestParam long id, @RequestParam String text, @RequestParam int rating) {

        Optional<Comment> c = commentService.findById(id);
        if (c.isPresent()) {
            Comment comment = c.get();
            comment.settext(text);
            comment.setRating(rating);
            commentService.save(comment);
        }
        return "redirect:/";
	}


    @GetMapping("/list-comments")
	public String showComments(Model model) {

		model.addAttribute("comments", commentService.findAll());

		return "comment-list";
	}
    @GetMapping("/remove-comment-admin/{id}")
	public String removeCommentAdmin(Model model, @PathVariable long id) {
        commentService.delete(id);
        return "redirect:/";
	}

    @GetMapping("/remove-comment/{id}")
	public String removeComment(Model model, @PathVariable long id) {
        Optional<Comment> c = commentService.findById(id);
        c.ifPresent(comment -> {
            commentService.delete(id);
        });
        return "redirect:/";
	}
}