package com.greentechpay.notificationservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    //TODO move secret  key to app secrets
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("mysecuritykeymysecuritykeymysecuritykeymysecuritykey"
            .getBytes(StandardCharsets.UTF_8));

    private final JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build();

    public Claims extractClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }
    }

    public String extractClaimByName(String token, String claimName) {
        final Claims claims = extractClaims(token);
        return claims.get(claimName, String.class);
    }


    public String extractUserId(String token) {
        String claimName = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier";
        return extractClaimByName(token, claimName);
    }
}
