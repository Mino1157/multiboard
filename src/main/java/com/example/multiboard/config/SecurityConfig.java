package com.example.multiboard.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf((csrfConfig) -> csrfConfig.disable());
		http.formLogin(login -> login.loginPage("/member/login")
				 								.usernameParameter("userid")
				 								.defaultSuccessUrl("/"));
		http.logout(logout -> logout.logoutUrl("/member/logout")
							  .logoutSuccessUrl("/member/login")
							  .invalidateHttpSession(true));
		http.authorizeHttpRequests(authRequest -> authRequest
			  .requestMatchers("/file/**").hasRole("ADMIN")
			  .requestMatchers("/board/**").hasAnyRole("USER","ADMIN")
			  .requestMatchers("/css/**","/js/**","/images/**").permitAll()
			  .requestMatchers("/member/insert").permitAll()
			  .requestMatchers("/member/login").permitAll()
			  .requestMatchers("/**").permitAll());
			  return http.build();
		
	}
	@Bean
	@ConditionalOnMissingBean(UserDetailsService.class)
	InMemoryUserDetailsManager userDetailsService() {
		return new InMemoryUserDetailsManager(
				User.withUsername("foo").password("{noop}demo").roles("ADMIN").build(),
				User.withUsername("bar").password("{noop}demo").roles("USER").build(),
				User.withUsername("ted").password("{noop}demo").roles("USER",
						"admin").build());
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
