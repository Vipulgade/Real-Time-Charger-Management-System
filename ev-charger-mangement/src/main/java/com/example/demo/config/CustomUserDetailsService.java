package com.example.demo.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User1;
import com.example.demo.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
   
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User1> user = userRepository.findByUsername(username);
        		
        return User.builder()
                .username(user.get().getUsername())
                .password(user.get().getPassword()) // Password should already be encoded
               // Assuming you store roles as a String or Enum
                .build();
    }



}
