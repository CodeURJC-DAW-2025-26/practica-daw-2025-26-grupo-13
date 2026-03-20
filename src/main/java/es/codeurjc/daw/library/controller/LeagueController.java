package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.League;
import es.codeurjc.daw.library.model.Marble;
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
		// If the user is logged in, add their name ,role and marbles to the model
		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
			userRepository.findByName(principal.getName()).ifPresent(user -> {
				model.addAttribute("userid", user.getId());
				model.addAttribute("userEmail", user.getEmail());
				List<Marble> marbles = user.getMarbles();
				Boolean found = false;
				for (Marble marble : marbles) {
					if (marble.isChosen()) {
						model.addAttribute("marble", marble.getName());
						found = true;
						break;
					}
				}
				if (!found) {
					model.addAttribute("marble", "Sin canica elegida");
				}
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
		// Show the first 3 leagues in the home page, and if there are more than 3 show a button to load more leagues
		Page<League> leaguesPage = leagueService.findAll(PageRequest.of(0, 3));
		
		model.addAttribute("leagues", leaguesPage.getContent());
		model.addAttribute("hasMore", leaguesPage.hasNext()); // this attribute will be used to show the load more button if there are more leagues to load
		
		return "home";
	}
	// Endpoint to load more leagues in the home page, it will return a list of leagues in json format
	@GetMapping("/api/leagues")
	@ResponseBody
	public ResponseEntity<List<League>> getMoreLeagues(@RequestParam int page) {
		Page<League> leaguesPage = leagueService.findAll(PageRequest.of(page, 3));
		return ResponseEntity.ok(leaguesPage.getContent());
}

	@GetMapping("/league/{id}")
	public String showLeague(Model model, Principal principal, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		// If the league is found, add it to the model and show the league view, otherwise redirect to home
		if (league.isPresent()) {
			League l = league.get();
			model.addAttribute("league", l);

			String statusMsg = l.getStatus() ? "Abierta" : "Finalizada";
			model.addAttribute("statusMsg", statusMsg);

			model.addAttribute("races", l.getRaces());

			// 1. obtain the current user id if the user is logged in, otherwise set it to -1
        	long currentUserId = -1;
        	if (principal != null) {
            	currentUserId = userRepository.findByName(principal.getName()).map(u -> u.getId()).orElse(-1L);
        }

        	// 2.  for each comment in the league, check if the current user is the owner of the comment, if yes then set the editAllowed attribute of the comment to true, otherwise set it to false
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

	@GetMapping( "/league-list-filtered")
	public String listLeaguesFiltered(Model model) {

		model.addAttribute("leagues", leagueService.findFiltered());

		return "league-list";
	}

	@GetMapping("/create-league")
	public String showCreateLeagueForm(Model model) {
		return "create-league";
	}

	@PostMapping("/create-league")
	public String newLeague(Model model, @RequestParam String name) throws IOException {
        
		// Create a new league with the given name and save it, then redirect to the league view
        League league = new League(name);

		leagueService.save(league);

		model.addAttribute("leagueId", league.getId());

		return "redirect:/league/" + league.getId();
	}

	@GetMapping("/edit-league/{id}")
	public String showEditLeagueForm(Model model, @PathVariable long id) {
		Optional<League> league = leagueService.findById(id);
		// If the league is found, add it to the model and show the edit league form, otherwise redirect to home
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
		// If the league is found, find the race with the given raceId and rename it, then save the league, otherwise redirect to league list
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
		// If the league is not found redirect to league list
		Optional<League> leagueOpt = leagueService.findById(leagueId);
		if (leagueOpt.isEmpty()) {
			return "redirect:/league-list";
		}
	
		// If the league is found, find the race with the given raceId 
		League league = leagueOpt.get();
		if (league.getRaces() != null) {
			for (Race race : league.getRaces()) {
				if (race.getId() != null && race.getId() == raceId) {
					// If the race is not finished and has users, calculate the results and update the win/lose counters of the users, then save the league
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
		// if the race  is found with the given raceId remove it from the league and  then save the league and remove the race
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
		// If the league is found, update its name and status, then save it, otherwise redirect to home
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
