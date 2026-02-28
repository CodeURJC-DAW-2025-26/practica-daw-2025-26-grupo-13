package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



import es.codeurjc.daw.library.model.League;
import es.codeurjc.daw.library.service.LeagueService;

@Controller
public class LeagueController {

	@Autowired
	private LeagueService leagueService;

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
	}

	@GetMapping("/")
	public String showLeagues(Model model) {

		model.addAttribute("leagues", leagueService.findAll()); //rellenar con ligas el html home (meter mustache "leagues")

		return "home";
	}

	@GetMapping("/league/{id}")
	public String showBook(Model model, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			model.addAttribute("league", league.get());
			return "league";
		} else {
			return "league-view";
		}
	}

	@PostMapping("/removeLeague/{id}")
	public String removeleague(Model model, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			leagueService.delete(id);
			model.addAttribute("league", league.get());
		}
		return "league-removed";
	}

	@GetMapping("/listLeagues")
	public String listLeagues(Model model) {

		model.addAttribute("leagueList", leagueService.findAll());

		return "league-list";
	}

	@PostMapping("/newLeague")
	public String newLeague(Model model, League league,
			@RequestParam String name) throws IOException {
        
        league = new League(name);

		leagueService.save(league);

		model.addAttribute("leagueId", league.getId());

		return "redirect:/league-view/" + league.getId();
	}

	@PostMapping("/editLeague")
	public String editBookProcess(Model model, League league, String name, boolean status)
			throws IOException, SQLException {

		league.setName(name);
        league.setStatus(status);

		leagueService.save(league);

		model.addAttribute("leagueId", league.getId());

		return "redirect:/league-view/" + league.getId();
	}

	@GetMapping("/playLeague/{id}")
	public String playLeague(Model model, @PathVariable long id) {

		Optional<League> league = leagueService.findById(id);
		if (league.isPresent()) {
			model.addAttribute("league", league.get());
			return "league";
		} else {
			return "league-view";
		}
	}

}
