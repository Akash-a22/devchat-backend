package com.devchat.devchat_room.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String key = "M2YyZGE0NzYzN2E2ZjdlMzAzN2ExZTQ2NTIwMTYxNjM=\n";


    public String generateToken(String name,String userId) {
        return Jwts.builder()
                .setSubject(name)
                .setHeaderParam("userID",userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
    public Authentication getAuthentication(String token) {
        String username = extractUsername(token);
        if (username == null) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
    }
}
