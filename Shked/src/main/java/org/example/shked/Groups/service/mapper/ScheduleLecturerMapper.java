package org.example.shked.Groups.service.mapper;


import org.example.shked.Groups.web.DTOs.lecturerDTOs.TeacherDailyScheduleDTO;
import org.example.shked.Groups.web.DTOs.lecturerDTOs.TeacherLessonDTO;
import org.example.shked.Groups.web.DTOs.lecturerDTOs.TeacherScheduleDTO;
import org.example.shked.Groups.DAL.entities.DailySchedule;
import org.example.shked.Groups.DAL.entities.Lesson;
import org.example.shked.Groups.DAL.entities.TeacherSchedule;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleLecturerMapper {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm:ss");

    public static TeacherScheduleDTO toDTO(TeacherSchedule teacherSchedule) {
        TeacherScheduleDTO dto = new TeacherScheduleDTO();
        dto.setTeacherId(teacherSchedule.getTeacherId());
        dto.setTeacherName(teacherSchedule.getTeacherName());

        List<TeacherDailyScheduleDTO> dailyScheduleDTOS = teacherSchedule.getDailySchedules().stream()
                .map(ScheduleLecturerMapper::toDTO)
                .collect(Collectors.toList());
        dto.setDailySchedules(dailyScheduleDTOS);

        dto.setUpdatedAt(teacherSchedule.getUpdatedAt());
        return dto;
    }

    public static TeacherDailyScheduleDTO toDTO(DailySchedule dailySchedule) {
        TeacherDailyScheduleDTO dto = new TeacherDailyScheduleDTO();
        dto.setDate(dailySchedule.getDate());
        dto.setWeekday(dailySchedule.getWeekday());
        dto.setLessons(
                dailySchedule.getLessons().stream()
                        .map(ScheduleLecturerMapper::toDTO)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public static TeacherLessonDTO toDTO(Lesson lesson) {
        TeacherLessonDTO dto = new TeacherLessonDTO();
        dto.setOrdinal(lesson.getOrdinal());
        dto.setName(lesson.getName());
        dto.setType(lesson.getType());
        dto.setStartTime(lesson.getStartTime() != null ? lesson.getStartTime().format(timeFormatter) : null);
        dto.setEndTime(lesson.getEndTime() != null ? lesson.getEndTime().format(timeFormatter) : null);
        dto.setLocation(lesson.getLocation());
        dto.setLocationId(lesson.getLocationId());
        dto.setGroups(lesson.getGroupNames());
        return dto;
    }
}
