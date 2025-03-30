package com.example.demo.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User1;


public interface UserRepository extends JpaRepository<User1, Long> {
    //User1 findByUsername(String username);
    Optional<User1> findByUsername(String username);
}
