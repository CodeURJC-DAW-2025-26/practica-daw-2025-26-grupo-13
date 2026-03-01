package es.codeurjc.daw.library.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Controller
public class LoginWebController {
	
	@GetMapping("/login-form")
	public String login() {
		return "login-form";
	}

	@GetMapping("/login-error")
	public String loginerror() {
		return "login-error";
	}

	@PostMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response,
				RedirectAttributes redirectAttributes) {
		// grab the current authentication before Spring clears it
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			// collect authorities so we know what roles the user had
			boolean wasUser = auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch("ROLE_USER"::equals);
			boolean wasAdmin = auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch("ROLE_ADMIN"::equals);

			// you can log them, pass them as flash attributes, etc.
			redirectAttributes.addFlashAttribute("wasUser", wasUser);
			redirectAttributes.addFlashAttribute("wasAdmin", wasAdmin);

			// perform the logout so the session/context are invalidated
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		return "redirect:/";
	}
	
}
