package org.example.shkedtasks.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTaskDTO(
        @NotBlank String title,
        String description,
        @NotNull LocalDateTime deadline
) {}