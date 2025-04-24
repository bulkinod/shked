package org.example.shked.Groups.DAL.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.shked.Groups.DAL.enums.LessonType;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lessons")
@Getter
@Setter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String lecturer;

    private String lecturerId;

    private String location;

    private String locationId;

    private LocalTime startTime;

    private LocalTime endTime;

    private int ordinal;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "group_name")
    private Set<String> groupNames = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private LessonType type;

    @ManyToOne
    @JoinColumn(name = "daily_schedule_id", nullable = false)
    private DailySchedule dailySchedule;
}