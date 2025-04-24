package org.example.shked.Groups.DAL.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.shked.Groups.DAL.enums.Weekday;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_schedules")
@Getter
@Setter
public class DailySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Weekday weekday;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = true)
    private Schedule schedule;

    @OneToMany(mappedBy = "dailySchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "teacher_schedule_id", nullable = true)
    private TeacherSchedule teacherSchedule;
}