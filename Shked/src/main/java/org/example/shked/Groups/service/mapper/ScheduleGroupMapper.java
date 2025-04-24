package org.example.shked.Groups.service.mapper;

import org.example.shked.Groups.DAL.entities.DailySchedule;
import org.example.shked.Groups.DAL.entities.Lesson;
import org.example.shked.Groups.DAL.entities.Schedule;
import org.example.shked.Groups.web.DTOs.groupDTOs.DailyScheduleDTO;
import org.example.shked.Groups.web.DTOs.groupDTOs.LessonDTO;
import org.example.shked.Groups.web.DTOs.groupDTOs.ScheduleDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleGroupMapper {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm:ss");

    public static ScheduleDTO toDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setGroupName(schedule.getGroupName());
        dto.setUpdatedAt(schedule.getUpdatedAt());

        List<DailyScheduleDTO> dailyDTOs = schedule.getDailySchedules().stream()
                .map(ScheduleGroupMapper::toDTO)
                .collect(Collectors.toList());

        dto.setDailySchedules(dailyDTOs);
        return dto;
    }

    public static DailyScheduleDTO toDTO(DailySchedule ds) {
        DailyScheduleDTO dto = new DailyScheduleDTO();
        dto.setDate(ds.getDate());
        dto.setWeekday(ds.getWeekday());

        List<LessonDTO> lessonDTOs = ds.getLessons().stream()
                .map(ScheduleGroupMapper::toDTO)
                .collect(Collectors.toList());

        dto.setLessons(lessonDTOs);
        return dto;
    }

    public static LessonDTO toDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setOrdinal(lesson.getOrdinal());
        dto.setName(lesson.getName());
        dto.setLecturer(lesson.getLecturer());
        dto.setType(lesson.getType());
        dto.setLocation(lesson.getLocation());
        dto.setLecturerId(lesson.getLecturerId());
        dto.setLocationId(lesson.getLocationId());
        dto.setStartTime(lesson.getStartTime() != null ? lesson.getStartTime().format(timeFormatter) : null);
        dto.setEndTime(lesson.getEndTime() != null ? lesson.getEndTime().format(timeFormatter) : null);
        return dto;
    }




}
