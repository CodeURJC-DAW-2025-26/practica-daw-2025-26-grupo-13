package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Marble;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.MarbleService;
//import es.codeurjc.daw.library.service.ImageService;


@Controller
public class MarbleController {

	@Autowired
	private MarbleService marbleService;

	@Autowired
	private UserRepository userRepository;

	//@Autowired
	//private ImageService imageService;

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

	@GetMapping("/marbles/{id}")
	public String showUserMarbles(Model model, @PathVariable long id) {

		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			List<Marble> marbles = user.get().getMarbles();
			model.addAttribute("marbles", marbles);
			
			int emptySlotsCount = Math.max(0, 3 - marbles.size());
			if (emptySlotsCount > 0) {
				List<Boolean> emptySlots = new ArrayList<>();
				for (int i = 0; i < emptySlotsCount; i++) {
					emptySlots.add(true);
				}
				model.addAttribute("emptySlots", emptySlots);
			}

			return "marbles-view";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/chooseMarble/{id}")
	public String chooseUserMarble(Model model, @PathVariable long id) {

		Optional<Marble> marble = marbleService.findById(id);
		if (marble.isPresent()) {
			marble.get().setChosen(true);
	//		model.addAttribute("marbles", user.get().getMarbles());
			return "marbles-view";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/removeMarble/{id}")
	public String removeMarble(HttpServletRequest request, Model model, @PathVariable long id) {

		Optional<Marble> marble = marbleService.findById(id);
		if (marble.isPresent()) {
			Principal principal = request.getUserPrincipal();
			if (principal != null) {
				Optional<User> opUser = userRepository.findByName(principal.getName());
				if (opUser.isPresent()) {
					User user = opUser.get();
					user.getMarbles().removeIf(m -> m.getId().equals(id));
					userRepository.save(user);
				} else {
					marbleService.delete(id);
				}
			} else {
				marbleService.delete(id);
			}
			model.addAttribute("marble", marble.get());
		}
		return "redirect:/";
	}

	@GetMapping("/listMarbles")
	public String listMarbles(Model model) {

		model.addAttribute("marbleList", marbleService.findAll());

		return "marbles-view";
	}

	@GetMapping("/newMarble")
	public String createMarbleForm(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			Optional<User> user = userRepository.findByName(principal.getName());
			if (user.isPresent()) {
				model.addAttribute("userId", user.get().getId());
			}
		}
		return "create-marble";
	}

	@PostMapping("/newMarble")
	public String newMarble(HttpServletRequest request, Model model,
			@RequestParam String name, Image image) throws IOException {

		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			Optional<User> opUser = userRepository.findByName(principal.getName());
			if (opUser.isPresent()) {
				User user = opUser.get();
				Marble marble = new Marble(name, image, user.getId());
				
				user.getMarbles().add(marble);
				userRepository.save(user);

				model.addAttribute("marbleId", marble.getId());
				return "redirect:/marbles/" + user.getId();
			}
		}

		// Fallback if not logged in or user not found
        Marble marble = new Marble(name, image, null);
		marbleService.save(marble);
		model.addAttribute("marbleId", marble.getId());

		return "redirect:/marble/" + marble.getId();
	}

	@PostMapping("/editMarble/{id}")
	public String editBookProcess(Model model, Marble marble, String name, boolean status)
			throws IOException, SQLException {

		marble.setName(name);

		marbleService.save(marble);

		model.addAttribute("marbleId", marble.getId());

		return "redirect:/marble/" + marble.getId();
	}

	/*private void updateImage(Marble marble, boolean removeImage, MultipartFile imageField)
			throws IOException, SQLException {

		if (!imageField.isEmpty()) {
			Marble dbMarble = marbleService.findById(marble.getId()).orElseThrow();

			if (dbMarble.getImage() == null) {
				Image image = imageService.createImage(imageField.getInputStream());
				marble.setImage(image);
			} else {
				Image image = imageService.replaceImageFile(dbMarble.getImage().getId(), imageField.getInputStream());
				marble.setImage(image);
			}
		} else {
			if (removeImage) {
				if (marble.getImage() != null) {
					imageService.deleteImage(marble.getImage().getId());
					marble.setImage(null);
				}
			} else {
				// Maintain the same image loading it before updating the marble
				Marble dbMarble = marbleService.findById(marble.getId()).orElseThrow();
				marble.setImage(dbMarble.getImage());
			}
		}
	} */

}
