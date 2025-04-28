package org.example.shkedtasks.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.example.shkedtasks.DTOs.CreateTaskDTO;
import org.example.shkedtasks.DTOs.TaskDTO;
import org.example.shkedtasks.DTOs.UpdateTaskDTO;
import org.example.shkedtasks.entities.TaskEntity;
import org.example.shkedtasks.exceptions.*;
import org.example.shkedtasks.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskDTO createTask(CreateTaskDTO dto, String userId, String groupNameEncoded) {
        if (groupNameEncoded == null || groupNameEncoded.isEmpty()) {
            throw new MissingGroupNameException("Missing X-Group-Name header");
        }

        String groupName;
        try {
            groupName = new String(Base64.getDecoder().decode(groupNameEncoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new InvalidGroupNameEncodingException("Invalid Base64 encoding for group name");
        }

        if (dto.deadline().isBefore(LocalDateTime.now())) {
            throw new InvalidDeadlineException("Deadline must be in the future");
        }

        var task = new TaskEntity();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setDeadline(dto.deadline());
        task.setGroupName(groupName);
        task.setCreatedByUserId(userId);

        taskRepository.save(task);

        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline()
        );
    }

    public List<TaskDTO> getTasksForGroup(String groupName) {
        return taskRepository.findAllByGroupName(groupName).stream()
                .map(task -> new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDeadline()))
                .toList();
    }

    @Transactional
    public void deleteTask(String taskId, String userId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getCreatedByUserId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
    }

    @Transactional
    public TaskDTO updateTask(String taskId, UpdateTaskDTO dto, String userId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getCreatedByUserId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to update this task");
        }

        if (dto.deadline().isBefore(LocalDateTime.now())) {
            throw new InvalidDeadlineException("Deadline must be in the future");
        }

        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setDeadline(dto.deadline());

        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDeadline());
    }


}
