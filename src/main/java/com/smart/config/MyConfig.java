package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {

	/*
	 * @Autowired private AuthenticationSuccessHandler customSuccessHandler;
	 */

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());

		return daoAuthenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
		return authConfiguration.getAuthenticationManager();
	}

	
	  @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
		  http.authorizeHttpRequests( authz -> authz
				  .requestMatchers("/admin/**").hasRole("ADMIN")
				  .requestMatchers("/user/**").hasRole("USER")
				  .requestMatchers("/**").permitAll() 
				  .anyRequest()
				  .authenticated()
	  
				);
	  
		  http.formLogin(form -> form
			.loginPage("/signin")
			.loginProcessingUrl("/login") 
			.defaultSuccessUrl("/user/index")
			//.failureUrl("/login-fail")
			//.successHandler(customSuccessHandler)
			 );
	  
	  http.csrf(csrf -> csrf.disable());
	  
	  return http.build();
	  
	  }
	 
	/*
	 * @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws
	 * Exception { http.csrf().disable() .authorizeHttpRequests()
	 * .requestMatchers("/admin/**") .hasRole("ADMIN") .requestMatchers("/user/**")
	 * .hasRole("USER") .requestMatchers("/**") .permitAll() .anyRequest()
	 * .authenticated() .and() .formLogin();
	 * 
	 * return http.build(); }
	 */

}
