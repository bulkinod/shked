package org.example.shked.Groups.web.DTOs.lecturerDTOs;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TeacherScheduleDTO {
    private String teacherName;

    private String teacherId;

    private List<TeacherDailyScheduleDTO> dailySchedules = new ArrayList<>();

    private LocalDateTime updatedAt;
}
