package org.example.shkedtasks.repositories;

import org.example.shkedtasks.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, String> {

    List<TaskEntity> findAllByGroupName(String groupName);
}