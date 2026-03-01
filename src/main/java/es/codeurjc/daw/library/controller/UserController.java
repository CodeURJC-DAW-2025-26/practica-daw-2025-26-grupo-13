package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ImageService;
import es.codeurjc.daw.library.service.UserService;


@Controller
public class UserController {

	@Autowired
	private ImageService imageService;
    @Autowired
	private UserService userService;

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

	@GetMapping("/register")
	public String showRegisterForm(Model model) {

		return "register-form";
	}
    @PostMapping("/register")
	public String newUserProcess(Model model, User user, MultipartFile imageField, HttpServletRequest request) throws IOException {

		if (userService.existByName(user.getName())) {
			model.addAttribute("registerError", "Ese nombre de usuario ya existe.");
			return "register-form";
		}

		String rawPassword = user.getEncodedPassword();

		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			userService.setUserImage(user, image);
		}

		userService.registerUser(user);

		model.addAttribute("userId", user.getId());

		try {
			request.login(user.getName(), rawPassword);
		} catch (ServletException exception) {
			model.addAttribute("registerError", "Usuario creado, pero no se pudo iniciar sesión automáticamente. Inicia sesión manualmente.");
			return "login-form";
		}

		return "redirect:/";
	}
	

	@GetMapping("/edit-user/{id}")
	public String showUser(Model model, @PathVariable long id) {

		Optional<User> user = userService.findById(id);
		if (user.isPresent()) {
			model.addAttribute("user", user.get());
			return "edit-user";
		} else {
			return "redirect:/login";
		}

	}

    @PostMapping("/edit-user")
	public String editUserProcess(Model model, User user, boolean removeImage, MultipartFile imageField)
			throws IOException, SQLException {

		updateImage(user, removeImage, imageField);

		userService.save(user);

		model.addAttribute("userId", user.getId());

		return "redirect:/home/" + user.getId();
	}
    @GetMapping("/user-list")
	public String showUserList(Model model) {

		List<User> users = userService.findAll();
		model.addAttribute("users", users);
			return "user-list";
	}
	@GetMapping("/user-ranking")
	public String showUserRanking(Model model) {

		List<User> users = userService.findAll();  // Aquí puedes implementar la lógica para ordenar a los usuarios por su puntuación
		model.addAttribute("users", users);
			return "user-ranking";
	}  
	@GetMapping("/statistics/{id}")
	public String showUserStatistics(Model model, @PathVariable long id) {

		Optional<User> user = userService.findById(id);
		if (user.isPresent()) {
			model.addAttribute("user", user.get());
			return "user-statistics";
		} else {
			return "redirect:/login";
		}

	}


	private void updateImage(User user, boolean removeImage, MultipartFile imageField)
			throws IOException, SQLException {

		if (!imageField.isEmpty()) {
			User dbUser = userService.findById(user.getId()).orElseThrow();

			if (dbUser.getImage() == null) {
				Image image = imageService.createImage(imageField.getInputStream());
				user.setImage(image);
			} else {
				Image image = imageService.replaceImageFile(dbUser.getImage().getId(), imageField.getInputStream());
				user.setImage(image);
			}
		} else {
			if (removeImage) {
				if (user.getImage() != null) {
					imageService.deleteImage(user.getImage().getId());
					user.setImage(null);
				}
			} else {
				// Maintain the same image loading it before updating the user
				User dbUser = userService.findById(user.getId()).orElseThrow();
				user.setImage(dbUser.getImage());
			}
		}
	}

}