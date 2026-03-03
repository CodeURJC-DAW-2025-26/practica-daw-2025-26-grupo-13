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

import es.codeurjc.daw.library.model.League;
import es.codeurjc.daw.library.model.Race;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.LeagueService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LeagueController {

    private final UserRepository userRepository;


	@Autowired
	private LeagueService leagueService;

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
			userRepository.findByName(principal.getName()).ifPresent(user -> model.addAttribute("userid", user.getId()));

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
	public String showLeague(Model model, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			model.addAttribute("league", league.get());

			String statusMsg = league.get().getStatus() ? "Abierta" : "Finalizada";
			model.addAttribute("statusMsg", statusMsg);

			List<Race> races = league.get().getRaces();
			model.addAttribute("races", races);

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
			return "edit-league";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/edit-league/{id}")
	public String editLeagueProcess(Model model, League league, String name, boolean status)
			throws IOException, SQLException {

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
