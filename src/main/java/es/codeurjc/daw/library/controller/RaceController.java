package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.Marble;
import es.codeurjc.daw.library.model.Race;
import es.codeurjc.daw.library.repository.LeagueRepository;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.RaceService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RaceController {

	private final UserRepository userRepository;
	private final LeagueRepository leagueRepository;

	@Autowired
	private RaceService raceService;

	RaceController(UserRepository userRepository, LeagueRepository leagueRepository) {
		this.userRepository = userRepository;
		this.leagueRepository = leagueRepository;
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

	@GetMapping("/race/{id}")
	public String showRace(Model model, Principal principal, @PathVariable long id, @RequestParam(name = "needMarble", required = false) String needMarble) {

		Optional<Race> race = raceService.findById(id);
		if (race.isPresent()) {
			Race currentRace = race.get();
			boolean leagueOpen = leagueRepository.findByRaces_Id(id)
					.map(l -> l.getStatus())
					.orElse(true);

			model.addAttribute("race", currentRace);
			model.addAttribute("results", currentRace.getResults());
			model.addAttribute("winnerName", currentRace.getWinnerName());
			model.addAttribute("raceFinished", currentRace.isFinished());
			model.addAttribute("raceFull", currentRace.getUsers() != null && currentRace.getUsers().size() >= 8);
			model.addAttribute("leagueFinished", !leagueOpen);

			boolean alreadyJoined = false;
			boolean canJoinRace = false;
			// If the user is logged in, check if they have already joined in the race and if they can join
			if (principal != null) {
				userRepository.findByName(principal.getName()).ifPresent(user -> {
					long userId = user.getId();
					boolean joined = currentRace.getUsers() != null && currentRace.getUsers().stream()
							.anyMatch(u -> u.getId() == userId);
					model.addAttribute("alreadyJoined", joined);
					boolean hasRoom = currentRace.getUsers() == null || currentRace.getUsers().size() < 8;
					model.addAttribute("canJoinRace", leagueOpen && !currentRace.isFinished() && !joined && hasRoom);
				});
			}
			// Defaults when not logged or user not found
			if (!model.containsAttribute("alreadyJoined")) {
				model.addAttribute("alreadyJoined", alreadyJoined);
			}
			if (!model.containsAttribute("canJoinRace")) {
				model.addAttribute("canJoinRace", canJoinRace);
			}

			// propagate needMarble flag to template when requested
			if (needMarble != null && !needMarble.isEmpty()) {
				model.addAttribute("needMarble", true);
			}
			
			return "race-view";
		} else {
			return "redirect:/";
		}

	}

	@PostMapping("/race/{id}/join")
	public String joinRace(Principal principal, @PathVariable long id) {

		if (principal == null) {
			return "redirect:/login-form";
		}

		Optional<Race> raceOpt = raceService.findById(id);
		if (raceOpt.isEmpty()) {
			return "redirect:/";
		}

		Race race = raceOpt.get();
		// Check if the league is still open for joining  or finished and redirects to the correct page 
		boolean leagueOpen = leagueRepository.findByRaces_Id(id)
				.map(l -> l.getStatus())
				.orElse(true);
		if (!leagueOpen) {
			return "redirect:/race/" + id;
		}
		if (race.isFinished()) {
			return "redirect:/race/" + id;
		}

		final boolean[] hasChosen = {false};
		Optional<es.codeurjc.daw.library.model.User> opUser = userRepository.findByName(principal.getName());
		if (opUser.isPresent()) {
			es.codeurjc.daw.library.model.User user = opUser.get();
			if (user.getMarbles() != null) {
				for (Marble m : user.getMarbles()) {
					if (m.isChosen()) {
						hasChosen[0] = true;
						break;
					}
				}
			}
			if (!hasChosen[0]) {
				// redirect back to race view with flag to show message
				return "redirect:/race/" + id + "?needMarble=true";
			}
			try {
				race.addUser(user);
				raceService.save(race);
			} catch (IllegalStateException ignored) {
				// Race full / already finished / etc.
			}
		}
		return "redirect:/race/" + id;
	}

	@PostMapping("/removeRace/{id}")
	public String removerace(Model model, @PathVariable long id) {

		Optional<Race> race = raceService.findById(id);
		// check if the race exists before deleting it
		if (race.isPresent()) {
			raceService.delete(id);
			model.addAttribute("race", race.get());
		}
		return "redirect:/";
	}

	@GetMapping({ "/race-list", "/listRaces" })
	public String listRaces(Model model) {

		model.addAttribute("raceList", raceService.findAll());

		return "redirect:/";
	}

	@PostMapping("/newRace")
	public String newRace(Model model, Race race, @RequestParam String name) throws IOException {
        
        race = new Race(name);

		raceService.save(race);

		model.addAttribute("raceId", race.getId());

		return "redirect:/race/" + race.getId();
	}

	@PostMapping("/editRace")
	public String editBookProcess(Model model, Race race, String name, boolean status)
			throws IOException, SQLException {

		race.setName(name);

		raceService.save(race);

		model.addAttribute("raceId", race.getId());

		return "redirect:/race/" + race.getId();
	}

}