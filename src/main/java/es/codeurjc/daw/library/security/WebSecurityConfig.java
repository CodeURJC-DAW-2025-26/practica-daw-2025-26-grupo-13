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
						// PUBLIC PAGES
						.requestMatchers("/").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/login-form").permitAll()
						.requestMatchers("/user-ranking").permitAll()
						.requestMatchers("/league-view/*").permitAll()
						.requestMatchers("/race-view/*").permitAll()
						.requestMatchers("/images/**").permitAll()
						.requestMatchers("/favicon.ico").permitAll()
						// PRIVATE PAGES
						.requestMatchers("/statistics/*").hasAnyRole("USER")
						.requestMatchers("/edit-user/*").hasAnyRole("USER")
						.requestMatchers("/edit-marble/*").hasAnyRole("USER")
						.requestMatchers("/create-league").hasAnyRole("ADMIN")
						.requestMatchers("/league-list").hasAnyRole("ADMIN")
						.requestMatchers("/user-list").hasAnyRole("ADMIN"))
				.formLogin(formLogin -> formLogin
						.loginPage("/login-form")
						.failureUrl("/login-error")
						.defaultSuccessUrl("/")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}
}
