package org.example.shked.Groups.web.DTOs.GroupListDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupDTO {
    private String name;
    private String fac;
    private String level;
    private String course;
    private LocalDateTime updatedAt;
}
