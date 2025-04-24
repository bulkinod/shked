package org.example.shked.Groups.web.DTOs.groupDTOs;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ScheduleDTO {
    private String groupName;
    private LocalDateTime updatedAt;
    private List<DailyScheduleDTO> dailySchedules;
}
