package org.example.shkedauth.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.shkedauth.DTOs.AccessToken;
import org.example.shkedauth.DTOs.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.example.shkedauth.entities.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    public AccessToken generateAccessToken(User user) {
        AccessToken accessToken = new AccessToken();
        Date now = new Date();
        // 15 минут
        long accessTokenValidityMs = 15 * 60 * 1000;
        Date expiry = new Date(now.getTime() + accessTokenValidityMs);
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .addClaims(Map.of(
                        "id", user.getId()
                ))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
         accessToken.setAccessToken(token);
         accessToken.setExpiredAt(now);
         return accessToken;
    }

    public RefreshToken generateRefreshToken() {
        long refreshTokenValidityMs = 7 * 24 * 60 * 60 * 1000;
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccessToken(UUID.randomUUID().toString());
        refreshToken.setExpiredAt(new Date(System.currentTimeMillis() + refreshTokenValidityMs));
        return refreshToken;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}

