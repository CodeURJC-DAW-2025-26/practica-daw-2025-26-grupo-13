package es.codeurjc.daw.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PRIVATE ACTIONS (must go before broad permitAll matchers)
						.requestMatchers("/race/*/join").hasAnyRole("USER", "ADMIN")
						// PUBLIC PAGES
						.requestMatchers("/error", "/error/**").permitAll()
						.requestMatchers("/security-error").permitAll()
						.requestMatchers("/api/leagues").permitAll()
						.requestMatchers("/").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/user-ranking").permitAll()
						.requestMatchers("/league/**").permitAll()
						.requestMatchers("/race/**").permitAll()
						.requestMatchers("/assets/**").permitAll()
						.requestMatchers("/css/**").permitAll()
						.requestMatchers("/favicon.ico").permitAll()					
						.requestMatchers("/images/**").permitAll()						
						// PRIVATE PAGES
						.requestMatchers("/statistics", "/statistics/", "/statistics/*").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/view-user", "/view-user/").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/view-user/marbles", "/view-user/marbles/").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/edit-user").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/edit-user-admin/", "/edit-user-admin/**").hasAnyRole("ADMIN")
						.requestMatchers("/create-league", "/create-league/").hasAnyRole("ADMIN")
						.requestMatchers("/edit-league/**").hasAnyRole("ADMIN")
						.requestMatchers("/league-list", "/league-list/", "/league-list-filtered").hasAnyRole("ADMIN")
						.requestMatchers("/user-list", "/user-list/").hasAnyRole("ADMIN")
						.requestMatchers("/remove-league", "/remove-league/**").hasAnyRole("ADMIN")
						.requestMatchers("/create-comment/**", "/create-comment").hasAnyRole("USER")
						.requestMatchers("/marbles").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/chooseMarble/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/removeMarble", "/removeMarble/**").hasAnyRole("USER", "ADMIN")		
						.requestMatchers("/newMarble", "/newMarble/**").hasAnyRole("USER", "ADMIN")						
						.requestMatchers("/editMarble/", "/editMarble/**").hasAnyRole("USER", "ADMIN")						
						.requestMatchers("/edit-comment/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/edit-comment-admin/**").hasAnyRole("ADMIN")
						.requestMatchers("/remove-comment/**").hasAnyRole("USER")
						.requestMatchers("/remove-comment-admin/**").hasAnyRole("ADMIN")
						.requestMatchers("/list-comments").hasAnyRole("ADMIN")
						.requestMatchers("/remove-user").hasAnyRole("USER")
						.requestMatchers("/remove-user-admin/**").hasAnyRole("ADMIN")
						.anyRequest().authenticated())

						.exceptionHandling(ex -> ex
								.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
								.accessDeniedHandler((request, response, accessDeniedException) -> {
									// Logged-in user without required role: send through custom error flow
									response.setStatus(HttpServletResponse.SC_FORBIDDEN);
									request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_FORBIDDEN);
									request.setAttribute("javax.servlet.error.message",
											"No tienes permisos suficientes para acceder a este contenido.");
									request.getRequestDispatcher("/security-error").forward(request, response);
								}))

				.formLogin(formLogin -> formLogin
						.loginPage("/login-form")
						.failureUrl("/login-error")
						.defaultSuccessUrl("/", true)
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}

	public static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authException) throws IOException, ServletException {
			String uri = request.getRequestURI();

			if (isProtectedPath(uri)) {
				// Real protected resource: return 403
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_FORBIDDEN);
				request.setAttribute("javax.servlet.error.message",
						"Acceso restringido. Inicia sesión con una cuenta autorizada.");
			} else {
				// Unknown or non-mapped URL for an anonymous user: treat as 404
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_NOT_FOUND);
				request.setAttribute("javax.servlet.error.message",
						"La página que buscas no existe o ha sido movida.");
			}

			request.getRequestDispatcher("/security-error").forward(request, response);
		}

		private static boolean isProtectedPath(String uri) {
			return uri != null && (
					uri.startsWith("/statistics") ||
					uri.startsWith("/view-user") ||
					uri.startsWith("/edit-user") ||
					uri.startsWith("/edit-user-admin") ||
					uri.startsWith("/create-league") ||
					uri.startsWith("/edit-league") ||
					uri.startsWith("/league-list") ||
					uri.startsWith("/user-list") ||
					uri.startsWith("/remove-league") ||
					uri.startsWith("/create-comment") ||
					uri.startsWith("/marbles") ||
					uri.startsWith("/chooseMarble") ||
					uri.startsWith("/removeMarble") ||
					uri.startsWith("/newMarble") ||
					uri.startsWith("/editMarble") ||
					uri.startsWith("/edit-comment") ||
					uri.startsWith("/edit-comment-admin") ||
					uri.startsWith("/remove-comment") ||
					uri.startsWith("/remove-comment-admin") ||
					uri.startsWith("/list-comments") ||
					uri.startsWith("/remove-user") ||
					uri.startsWith("/remove-user-admin") ||
					(uri.startsWith("/race/") && uri.endsWith("/join"))
			);
		}
	}
}
