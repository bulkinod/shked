package org.example.shked.Groups.web.DTOs.groupDTOs;

import lombok.Getter;
import lombok.Setter;
import org.example.shked.Groups.DAL.enums.LessonType;

@Getter
@Setter
public class LessonDTO {
    private int ordinal;
    private String name;
    private String lecturer;
    private LessonType type;
    private String location;
    private String startTime;
    private String endTime;
    private String locationId;
    private String lecturerId;
}
