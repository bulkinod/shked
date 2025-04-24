package org.example.shked.Groups.DAL.repositories;

import org.example.shked.Groups.DAL.entities.TeacherSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TeacherScheduleRepository extends JpaRepository<TeacherSchedule, Long> {
    Optional<TeacherSchedule> findByTeacherIdAndUpdatedAtAfter(String teacherId, LocalDateTime localDateTime);
    Optional<TeacherSchedule> findByTeacherId(String teacherId);

}
