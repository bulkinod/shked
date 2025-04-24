package org.example.shkedauth.mappers;

import org.example.shkedauth.DTOs.RefreshToken;
import org.example.shkedauth.entities.RefreshTokenEntity;

public class ToDtoMapper {
    public RefreshToken toDTO(RefreshTokenEntity refreshToken){
        RefreshToken dto = new RefreshToken();
        dto.setAccessToken(refreshToken.getToken());
        dto.setExpiredAt(refreshToken.getExpiryDate());
        return dto;
    }
}
