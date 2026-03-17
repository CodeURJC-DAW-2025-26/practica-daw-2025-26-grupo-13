package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.League;
import es.codeurjc.daw.library.model.Race;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.LeagueService;
import es.codeurjc.daw.library.service.RaceService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LeagueController {

    private final UserRepository userRepository;


	@Autowired
	private LeagueService leagueService;

	@Autowired
	private RaceService raceService;

    LeagueController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
			userRepository.findByName(principal.getName()).ifPresent(user -> {
				model.addAttribute("userid", user.getId());
				model.addAttribute("userEmail", user.getEmail());
				if (user.getImage() != null) {
					model.addAttribute("userImageId", user.getImage().getId());
				}
			});

		} else {
			model.addAttribute("logged", false);
		}
	}

	@GetMapping("/")
	public String showLeagues(Model model) {

		model.addAttribute("leagues", leagueService.findAll());

		return "home";
	}

	@GetMapping("/league/{id}")
	public String showLeague(Model model, Principal principal, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			League l = league.get();
			model.addAttribute("league", l);

			String statusMsg = l.getStatus() ? "Abierta" : "Finalizada";
			model.addAttribute("statusMsg", statusMsg);

			model.addAttribute("races", l.getRaces());

			// 1. Obtener el ID del usuario logueado actualmente
        	long currentUserId = -1;
        	if (principal != null) {
            	currentUserId = userRepository.findByName(principal.getName()).map(u -> u.getId()).orElse(-1L);
        }

        	// 2. Iterar sobre los comentarios de la liga y establecer 'editAllowed'
        	for (Comment comment : l.getComments()) {
            	boolean isOwner = (comment.getUser() != null && comment.getUser().getId() == currentUserId);
            	comment.setEditAllowed(isOwner); 
        	}

			List<Comment> comments = l.getComments();
			model.addAttribute("comments", comments);

			return "league-view";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/remove-league/{id}")
	public String removeLeague(Model model, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			leagueService.delete(id);
			model.addAttribute("league", league.get());
		}
		return "league-list";
	}

	@GetMapping("/remove-league/{id}")
	public String removeLeagueRedirect(Model model, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			leagueService.delete(id);
			model.addAttribute("league", league.get());
		}
		return "redirect:/league-list";
	}

	@GetMapping( "/league-list")
	public String listLeagues(Model model) {

		model.addAttribute("leagues", leagueService.findAll());

		return "league-list";
	}

	@GetMapping("/create-league")
	public String showCreateLeagueForm(Model model) {
		return "create-league";
	}

	@PostMapping("/create-league")
	public String newLeague(Model model, @RequestParam String name) throws IOException {
        
        League league = new League(name);

		leagueService.save(league);

		model.addAttribute("leagueId", league.getId());

		return "redirect:/league/" + league.getId();
	}

	@GetMapping("/edit-league/{id}")
	public String showEditLeagueForm(Model model, @PathVariable long id) {
		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			model.addAttribute("league", league.get());
			model.addAttribute("leagueId", league.get().getId());
			model.addAttribute("races", league.get().getRaces());
			return "edit-league";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/edit-league/{leagueId}/race/{raceId}/rename")
	public String renameRace(@PathVariable long leagueId, @PathVariable long raceId, @RequestParam String name) {

		Optional<League> leagueOpt = leagueService.findById(leagueId);
		if (leagueOpt.isEmpty()) {
			return "redirect:/league-list";
		}

		League league = leagueOpt.get();
		if (league.getRaces() != null) {
			for (Race race : league.getRaces()) {
				if (race.getId() != null && race.getId() == raceId) {
					race.setName(name);
					leagueService.save(league);
					break;
				}
			}
		}

		return "redirect:/edit-league/" + leagueId;
	}

	@PostMapping("/edit-league/{leagueId}/race/{raceId}/start")
	public String startRace(@PathVariable long leagueId, @PathVariable long raceId) {

		Optional<League> leagueOpt = leagueService.findById(leagueId);
		if (leagueOpt.isEmpty()) {
			return "redirect:/league-list";
		}

		League league = leagueOpt.get();
		if (league.getRaces() != null) {
			for (Race race : league.getRaces()) {
				if (race.getId() != null && race.getId() == raceId) {
					if (!race.isFinished() && race.getUsers() != null && !race.getUsers().isEmpty()) {
						race.calculateResults();
						if (race.getResults() != null && !race.getResults().isEmpty()) {
							long winnerId = race.getResults().get(0).getId();
							for (User user : race.getResults()) {
								if (user.getId() == winnerId) {
									user.incrementWinCounter();
								} else {
									user.incrementLoseCounter();
								}
							}
							userRepository.saveAll(race.getResults());
						}
						leagueService.save(league);
					}
					break;
				}
			}
		}

		return "redirect:/edit-league/" + leagueId;
	}

	@PostMapping("/edit-league/{leagueId}/race/{raceId}/delete")
	public String deleteRace(@PathVariable long leagueId, @PathVariable long raceId) {

		Optional<League> leagueOpt = leagueService.findById(leagueId);
		if (leagueOpt.isEmpty()) {
			return "redirect:/league-list";
		}

		League league = leagueOpt.get();
		boolean removed = false;
		if (league.getRaces() != null) {
			removed = league.getRaces().removeIf(r -> r.getId() != null && r.getId() == raceId);
		}
		if (removed) {
			leagueService.save(league);
			raceService.delete(raceId);
		}

		return "redirect:/edit-league/" + leagueId;
	}

	@PostMapping("/edit-league/{id}")
	public String editLeagueProcess(Model model, @PathVariable long id, @RequestParam String name,
			@RequestParam(name = "status", defaultValue = "false") boolean status) throws IOException, SQLException {

		Optional<League> leagueOpt = leagueService.findById(id);
		if (leagueOpt.isEmpty()) {
			return "redirect:/";
		}

		League league = leagueOpt.get();
		league.setName(name);
		league.setStatus(status);
		leagueService.save(league);

		model.addAttribute("leagueId", league.getId());
		return "redirect:/league/" + league.getId();
	}

	@GetMapping("/playLeague/{id}")
	public String playLeague(Model model, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			model.addAttribute("league", league.get());
			return "league-view";
		} else {
			return "redirect:/";
		}
	}

}
