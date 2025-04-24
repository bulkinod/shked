package org.example.shked.Groups.web.DTOs.lecturerDTOs;

import lombok.Getter;
import lombok.Setter;
import org.example.shked.Groups.DAL.enums.Weekday;


import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TeacherDailyScheduleDTO {
    private LocalDate date;
    private Weekday weekday;
    private List<TeacherLessonDTO> lessons;
}