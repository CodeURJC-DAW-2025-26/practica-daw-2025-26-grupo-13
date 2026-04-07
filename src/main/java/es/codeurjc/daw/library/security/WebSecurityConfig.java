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
						.requestMatchers("/api/leagues").permitAll()
						.requestMatchers("/").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/user-ranking").permitAll()
						.requestMatchers("/league/**").permitAll()
						.requestMatchers("/race/**").permitAll()
						.requestMatchers("/assets/**").permitAll()
						.requestMatchers("/favicon.ico").permitAll()
						.requestMatchers("/css/**").permitAll()
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
						.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))

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
			// Forward to error handler instead of redirecting to login
			request.getRequestDispatcher("/error").forward(request, response);
		}
	}
}
