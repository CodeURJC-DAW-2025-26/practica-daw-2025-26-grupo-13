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
//import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Marble;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.service.MarbleService;
//import es.codeurjc.daw.library.service.ImageService;


@Controller
public class MarbleController {

	@Autowired
	private MarbleService marbleService;

	//@Autowired
	//private ImageService imageService;

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

	@GetMapping("/marble/{id}")
	public String showBook(Model model, @PathVariable long id) {

		Optional<Marble> marble = marbleService.findById(id);
		if (marble.isPresent()) {
			model.addAttribute("marble", marble.get());
			return "marble";
		} else {
			return "marble-view";
		}

	}

	@PostMapping("/removeMarble/{id}")
	public String removeMarble(Model model, @PathVariable long id) {

		Optional<Marble> marble = marbleService.findById(id);
		if (marble.isPresent()) {
			marbleService.delete(id);
			model.addAttribute("marble", marble.get());
		}
		return "marble-removed";
	}

	@GetMapping("/listMarbles")
	public String listMarbles(Model model) {

		model.addAttribute("marbleList", marbleService.findAll());

		return "marble-list";
	}

	@PostMapping("/newMarble")
	public String newMarble(Model model, Marble marble,
			@RequestParam String name, User user, Image image) throws IOException {
        
        marble = new Marble(name, image, user.getId());

		marbleService.save(marble);

		model.addAttribute("marbleId", marble.getId());

		return "redirect:/marble-view/" + marble.getId();
	}

	@PostMapping("/editMarble")
	public String editBookProcess(Model model, Marble marble, String name, boolean status)
			throws IOException, SQLException {

		marble.setName(name);

		marbleService.save(marble);

		model.addAttribute("marbleId", marble.getId());

		return "redirect:/marble-view/" + marble.getId();
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
