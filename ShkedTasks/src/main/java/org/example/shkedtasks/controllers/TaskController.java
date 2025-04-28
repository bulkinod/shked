package org.example.shkedtasks.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.shkedtasks.DTOs.CreateTaskDTO;
import org.example.shkedtasks.DTOs.TaskDTO;
import org.example.shkedtasks.DTOs.UpdateTaskDTO;
import org.example.shkedtasks.services.TaskService;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskDTO createTask(@RequestBody @Valid CreateTaskDTO dto, HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        String groupName = request.getHeader("X-Group-Name");
        return taskService.createTask(dto, userId, groupName);
    }

    @GetMapping
    public List<TaskDTO> getTasks(HttpServletRequest request) {
        String groupName = request.getHeader("X-Group-Name");
        return taskService.getTasksForGroup(groupName);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id, HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        taskService.deleteTask(id, userId);
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable String id,
                              @RequestBody @Valid UpdateTaskDTO dto,
                              HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return taskService.updateTask(id, dto, userId);
    }
}
