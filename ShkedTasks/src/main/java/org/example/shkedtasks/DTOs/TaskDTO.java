package org.example.shkedtasks.DTOs;

import java.time.LocalDateTime;

public record TaskDTO(
        String id,
        String title,
        String description,
        LocalDateTime deadline
) {}