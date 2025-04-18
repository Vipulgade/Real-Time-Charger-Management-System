package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User1;
import com.example.demo.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initDefaultUsers() {
        if (userRepository.count() == 0) {
            User1 admin = new User1();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
           

            User1 user = new User1();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            
            userRepository.saveAll(List.of(admin, user));
        }
    }
}
