package com.example.demo.security;


import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.demo.entity.User1;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

	private static final String Secret ="secret";
	public String generateToken(User1 user) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("name",user.getUsername());
		Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		Instant expiration = issuedAt.plus(30,ChronoUnit.DAYS);
		//log.info("Creating JWT token");
		return Jwts.builder()
		.setClaims(claims)
		.setIssuer("com.ev")
		.setIssuedAt(Date.from(issuedAt))
		.setExpiration(Date.from(expiration))
		.compact();
	}

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        return getClaims(token).getExpiration().after(new Date());
    }

    private Claims getClaims(String token) {
    	 Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(Secret));
        return Jwts.parserBuilder()
                .setSigningKey(key) 
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

