package org.example.shkedauth.controllers;

import lombok.RequiredArgsConstructor;
import org.example.shkedauth.DTOs.AuthDTO;
import org.example.shkedauth.DTOs.RefreshRequestDTO;
import org.example.shkedauth.DTOs.SignInDTO;
import org.example.shkedauth.DTOs.SignUpDTO;
import org.example.shkedauth.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthDTO register(@RequestBody SignUpDTO dto) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    public AuthDTO login(@RequestBody SignInDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/refresh")
    public AuthDTO refresh(@RequestBody RefreshRequestDTO dto) {
        return authService.refresh(dto.refreshToken());
    }
}
