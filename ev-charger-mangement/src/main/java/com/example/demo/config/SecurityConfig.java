package com.example.demo.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



	    private final JwtUtil jwtUtil;
	    private final UserDetailsService userDetailsService;

	    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
	        this.jwtUtil = jwtUtil;
	        this.userDetailsService = userDetailsService;
	    }

	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http.csrf(csrf -> csrf.disable())  // Disable CSRF for H2 Console
	            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Allow H2 Console in iframe
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/api/auth/**", "/h2-console/**","/ws/**","/api/chargers/**","/api/transactions/**","/api/transactions/filter/**","/ocpp/**").permitAll() // Allow H2 console
	                .anyRequest().authenticated()
	            )
	            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), 
	                             UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	    }

	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	        return authenticationConfiguration.getAuthenticationManager();
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	}


