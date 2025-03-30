package com.example.demo;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.jsonwebtoken.security.Keys;

@SpringBootApplication
@EnableScheduling 
public class EvChargerManagement {

	public static void main(String[] args) {
		SpringApplication.run(EvChargerManagement.class, args);
		//   SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256); 
	       // String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
	}

}
