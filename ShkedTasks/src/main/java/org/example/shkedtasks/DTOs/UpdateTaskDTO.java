package org.example.shkedtasks.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateTaskDTO(
        @NotBlank(message = "Title cannot be blank")
        String title,

        String description,

        @NotNull(message = "Deadline cannot be null")
        LocalDateTime deadline
) {}
