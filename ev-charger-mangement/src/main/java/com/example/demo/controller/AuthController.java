package com.example.demo.controller;


import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User1;
import com.example.demo.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

   

   

       
       
        @PostMapping("/register")
        public ResponseEntity<String> register(@RequestBody User1 user) {
            try {
                logger.info("Registering user: {}", user.getUsername());
                authService.register(user);
                logger.info("User registered successfully: {}", user.getUsername());
                return ResponseEntity.ok("User registered successfully");
            } catch (RuntimeException e) {
                logger.error("Registration failed: User {} already exists", user.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
            }
        }

        @PostMapping("/login")
        public ResponseEntity<String> login(@RequestBody User1 user) {
            try {
                logger.info("Login attempt for user: {}", user.getUsername());
                String token = authService.login(user);
                logger.info("Login successful for user: {}", user.getUsername());
                return ResponseEntity.ok(token);
            } catch (NoSuchElementException | BadCredentialsException e) {
                logger.warn("Login failed for user {}: {}", user.getUsername(), e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error during login for user {}: {}", user.getUsername(), e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
            }
        }
    
}