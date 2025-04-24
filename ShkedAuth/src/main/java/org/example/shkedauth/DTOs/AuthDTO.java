package org.example.shkedauth.DTOs;

public record AuthDTO(
        AccessToken accessToken,
        RefreshToken refreshToken,
        String userId
) {}
