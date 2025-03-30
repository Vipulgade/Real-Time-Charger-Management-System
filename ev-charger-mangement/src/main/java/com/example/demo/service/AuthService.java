package com.example.demo.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User1;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtutil;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtutil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
		this.jwtutil = jwtutil;
    }

    public void register(User1 user) {
        Optional<User1> existingUser = userRepository.findByUsername(user.getUsername());
        
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public String login(User1 user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username and password must not be null");
        }
        
       
        Optional<User1> existingUser = userRepository.findByUsername(user.getUsername());

        // Properly check if the user exists
        if (existingUser.isEmpty()) {
            throw new NoSuchElementException("User not found with username: " + user.getUsername());
        }
        User1 foundUser = existingUser.get();
        System.out.println("User input password: " + user.getPassword());
        System.out.println("Stored password (hashed): " + foundUser.getPassword());
       boolean matches = passwordEncoder.matches(user.getPassword(), foundUser.getPassword());
       // User1 foundUser = existingUser.get();  // Safe to call get() now

        if (matches==false) {
            throw new  NoSuchElementException("Invalid username or password");
        }

     
        return jwtutil.generateToken(foundUser);
    }



   
}
