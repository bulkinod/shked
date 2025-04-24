package org.example.shked.Groups.web.controllers;

import org.example.shked.Groups.web.DTOs.GroupListDTO.GroupDTO;
import org.example.shked.Groups.web.DTOs.groupDTOs.ScheduleDTO;
import org.example.shked.Groups.web.DTOs.lecturerDTOs.TeacherScheduleDTO;
import org.example.shked.Groups.service.services.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/group/{groupName}")
    public ResponseEntity<ScheduleDTO> getSchedule(@PathVariable String groupName) {
        return ResponseEntity.ok(scheduleService.getGroupSchedule(groupName));
    }

    @GetMapping("/lecturer/{teacherUid}")
    public ResponseEntity<TeacherScheduleDTO> getTeacherSchedule(@PathVariable String teacherUid) {
        TeacherScheduleDTO dto = scheduleService.getTeacherSchedule(teacherUid);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/groupList")
    public ResponseEntity<List<GroupDTO>> getGroupList() {
        List<GroupDTO> groups = scheduleService.getAllGroups();
        return ResponseEntity.ok(groups);
    }
}
