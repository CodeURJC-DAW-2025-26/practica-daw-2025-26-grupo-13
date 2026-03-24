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
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Marble;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.ImageService;
import es.codeurjc.daw.library.service.MarbleService;
//import es.codeurjc.daw.library.service.ImageService;


@Controller
public class MarbleController {

	@Autowired
	private MarbleService marbleService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageService imageService;


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
				// check wich is the chosen marble and add it to the model
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

	@GetMapping("/marbles")
	public String showUserMarbles(Model model, Principal principal) {

		Optional<User> user = userRepository.findByName(principal.getName());
		// If the user is found, add their marbles to the model and show the marbles view, otherwise redirect to home
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
		// If the marble is found, set it as chosen and set all the other marbles of the user as not chosen, then redirect to home
		if (marble.isPresent()) {
			marble.get().setChosen(true);
			marbleService.save(marble.get());
			for (Marble m : marbleService.findAll()) {
				if (m.getUser() == marble.get().getUser() && m.getId() != id) {
					m.setChosen(false);
					marbleService.save(m);
				}
			}
		}
		return "/";
	}

	@PostMapping("/removeMarble/{id}")
	public String removeMarble(HttpServletRequest request, Model model, @PathVariable long id) {

		Optional<Marble> marble = marbleService.findById(id);
		// If the marble is found, check if  the marble has an owner or not, if yes then disown it and then delete the marble, if not then just delete the marble, then redirect to home
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

	@GetMapping("/newMarble")
	public String createMarbleForm(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		// If the user is not logged in, redirect to login, otherwise show the create marble form
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
			@RequestParam String name, @RequestParam(required = false) MultipartFile imageField) throws IOException {

		Principal principal = request.getUserPrincipal();
		Image image = null;
		// If the user is logged in, create the marble with the user as owner, otherwise create it without owner, then redirect to the marble view
		if (principal != null) {
			Optional<User> opUser = userRepository.findByName(principal.getName());
			if (opUser.isPresent()) {
				User user = opUser.get();
				if (!imageField.isEmpty()) {
					image = imageService.createImage(imageField.getInputStream());
				}
				Marble marble = new Marble(name, image, user.getId());
				
				user.getMarbles().add(marble);
				userRepository.save(user);

				model.addAttribute("marbleId", marble.getId());
				return "redirect:/marbles";
			}
		}
        Marble marble = new Marble(name, image, null);
		marbleService.save(marble);
		model.addAttribute("marbleId", marble.getId());

		return "redirect:/marble/" + marble.getId();
	}

	@GetMapping("/editMarble/{id}")
    public String editMarbleForm(HttpServletRequest request, Model model, @PathVariable long id) {
        Optional<Marble> marble = marbleService.findById(id);
		// If the marble is found, check if the user is the owner of the marble, if yes then show the edit form, otherwise redirect to home
        if (marble.isPresent()) {
			Principal principal = request.getUserPrincipal();
			if (principal != null) {
				Optional<User> opUser = userRepository.findByName(principal.getName());
				if (opUser.isPresent()) {
					User user = opUser.get();
					if (marble.get().getUser() != null && marble.get().getUser().equals(user.getId())) {
            			model.addAttribute("marble", marble.get());
            			return "edit-marble";
					}
        		} 
			}
    	}
		return "/marbles";
	}

    @PostMapping("/editMarble/{id}")
    public String editMarblePost(Model model, @PathVariable long id, 
            @RequestParam String name, 
            @RequestParam(required = false) MultipartFile imageField) throws IOException, SQLException {

        Optional<Marble> opMarble = marbleService.findById(id);
        
		// If the marble is found, check if the user is the owner of the marble, if yes then update the marble with the new name and image, otherwise redirect to home
        if (opMarble.isPresent()) {
            Marble marble = opMarble.get();
            
            marble.setName(name);

            if (imageField != null && !imageField.isEmpty()) {
                if (marble.getImage() == null) {
                    Image image = imageService.createImage(imageField.getInputStream());
                    marble.setImage(image);
                } else {
                    Image image = imageService.replaceImageFile(marble.getImage().getId(), imageField.getInputStream());
                    marble.setImage(image);
                }
            }

            marbleService.save(marble);
        }

        return "redirect:/marbles";
    }
	

	

}
