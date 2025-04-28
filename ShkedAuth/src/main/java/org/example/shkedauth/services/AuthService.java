package org.example.shkedauth.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.shkedauth.DTOs.*;
import org.example.shkedauth.entities.RefreshTokenEntity;
import org.example.shkedauth.entities.User;
import org.example.shkedauth.exceptions.*;
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
    private final GroupService groupService;



    @Transactional
    public AuthDTO register(SignUpDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String normalizedGroupName = groupService.validateAndGetNormalizedGroupName(dto.groupName());

        var user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(dto.email());
        user.setFullName(dto.fullName());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setGroupName(normalizedGroupName);
        userRepository.save(user);

        userRepository.flush();

        return createAuthResponse(user);
    }



    @Transactional
    public AuthDTO login(SignInDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Wrong password");
        }

        refreshTokenRepository.deleteAllByUserId(user.getId());

        return createAuthResponse(user);
    }
    @Transactional
    public AuthDTO refresh(String refreshToken) {
        var tokens = refreshTokenRepository.findAll();

        RefreshTokenEntity token = tokens.stream()
                .filter(t -> passwordEncoder.matches(refreshToken, t.getToken()))
                .findFirst()
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (token.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(token);
            throw new ExpiredRefreshTokenException("Refresh token expired");
        }

        refreshTokenRepository.delete(token);

        return createAuthResponse(token.getUser());
    }

    private AuthDTO createAuthResponse(User user) {
        AccessToken accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = jwtService.generateRefreshToken();

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setToken(passwordEncoder.encode(refreshToken.getToken()));
        entity.setExpiryDate(refreshToken.getExpiredAt());
        refreshTokenRepository.save(entity);

        return new AuthDTO(accessToken, refreshToken, user.getId());
    }
}
