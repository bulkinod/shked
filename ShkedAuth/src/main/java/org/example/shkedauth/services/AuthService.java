package org.example.shkedauth.services;

import lombok.RequiredArgsConstructor;
import org.example.shkedauth.DTOs.*;
import org.example.shkedauth.entities.RefreshTokenEntity;
import org.example.shkedauth.entities.User;
import org.example.shkedauth.repositories.RefreshTokenRepository;
import org.example.shkedauth.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthDTO register(SignUpDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email already exists");
        }

        var user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(dto.email());
        user.setFullName(dto.fullName());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        return createAuthResponse(user);
    }

    public AuthDTO login(SignInDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return createAuthResponse(user);
    }

    public AuthDTO refresh(String refreshToken) {
        RefreshTokenEntity token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        refreshTokenRepository.delete(token);

        return createAuthResponse(token.getUser());
    }

    private AuthDTO createAuthResponse(User user) {
        AccessToken accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = jwtService.generateRefreshToken();

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setToken(refreshToken.getAccessToken());
        entity.setExpiryDate(refreshToken.getExpiredAt());
        refreshTokenRepository.save(entity);

        return new AuthDTO(accessToken, refreshToken, user.getId());
    }
}
