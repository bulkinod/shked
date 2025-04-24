package org.example.shked.Groups.DAL.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class TeacherSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teacherName;

    @Column(unique = true, nullable = false)
    private String teacherId;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "teacherSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailySchedule> dailySchedules = new ArrayList<>();
}