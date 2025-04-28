package org.example.shkedauth.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpDTO(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        @Size(max = 254, message = "Email must be at most 254 characters")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]+$",
                message = "Password must contain at least one letter and one number"
        )
        String password,

        @NotBlank(message = "Full name cannot be blank")
        @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
        String fullName,

        @NotBlank(message = "Group name cannot be blank")
        String groupName
) {}