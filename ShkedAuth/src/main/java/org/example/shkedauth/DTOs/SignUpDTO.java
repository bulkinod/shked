package org.example.shkedauth.DTOs;

public record SignUpDTO(
        String email,
        String password,
        String fullName
) {}