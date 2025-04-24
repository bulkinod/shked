package org.example.shked.Groups.DAL.repositories;

import org.example.shked.Groups.DAL.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GroupScheduleRepository extends JpaRepository<Schedule,Long> {
    Optional<Schedule> findByGroupName(String groupName);
    Optional<Schedule> findByGroupNameAndUpdatedAtAfter(String groupName, LocalDateTime updatedAfter);
}
