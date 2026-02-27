package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Marble;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Race;
import es.codeurjc.daw.library.service.MarbleService;
import es.codeurjc.daw.library.service.ImageService;
import es.codeurjc.daw.library.service.RaceService;

@Controller
public class RaceController {

	@Autowired
	private RaceService raceService;

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

	@GetMapping("/race/{id}")
	public String showBook(Model model, @PathVariable long id) {

		Optional<Race> race = raceService.findById(id);
		if (race.isPresent()) {
			model.addAttribute("race", race.get());
			return "race";
		} else {
			return "race-view";
		}

	}

	@PostMapping("/removeRace/{id}")
	public String removerace(Model model, @PathVariable long id) {

		Optional<Race> race = raceService.findById(id);
		if (race.isPresent()) {
			raceService.delete(id);
			model.addAttribute("race", race.get());
		}
		return "race-removed";
	}

	@GetMapping("/listRaces")
	public String listRaces(Model model) {

		model.addAttribute("raceList", raceService.findAll());

		return "race-list";
	}

	@PostMapping("/newRace")
	public String newRace(Model model, Race race,
			@RequestParam String name) throws IOException {
        
        race(name);

		raceService.save(race);

		model.addAttribute("raceId", race.getId());

		return "redirect:/race-view/" + race.getId();
	}

	@PostMapping("/editRace")
	public String editBookProcess(Model model, Race race, String name, boolean status)
			throws IOException, SQLException {

		race.setName(name);
        race.setName(status);

		raceService.save(race);

		model.addAttribute("raceId", race.getId());

		return "redirect:/race-view/" + race.getId();
	}

	@GetMapping("/playRace/{id}")
	public String playRace(Model model, @PathVariable long id) {

		Optional<Race> race = raceService.findById(id);
		if (race.isPresent()) {
			model.addAttribute("race", race.get());
			return "race";
		} else {
			return "race-view";
		}

	}

}
