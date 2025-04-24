package org.example.shked.Groups.service.mapper;

import org.example.shked.Groups.web.DTOs.GroupListDTO.GroupDTO;
import org.example.shked.Groups.DAL.entities.Group;

public class GroupListMapper {
    public static GroupDTO toDTO(Group group){
        GroupDTO dto = new GroupDTO();
        dto.setName(group.getName());
        dto.setFac(group.getFac());
        dto.setLevel(group.getLevel());
        dto.setCourse(group.getCourse());
        dto.setUpdatedAt(group.getUpdatedAt());
        return dto;
    }
}
