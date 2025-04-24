package org.example.shked.Groups.web.DTOs.lecturerDTOs;

import lombok.Getter;
import lombok.Setter;
import org.example.shked.Groups.DAL.enums.LessonType;

import java.util.Set;

@Getter
@Setter
public class TeacherLessonDTO {
    private int ordinal;
    private String name;
    private LessonType type;
    private String location;
    private String locationId;
    private String startTime;
    private String endTime;
    private Set<String> groups;
}