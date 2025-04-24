package org.example.shked.Groups.service.services;

import jakarta.transaction.Transactional;
import org.example.shked.Groups.DAL.entities.Group;
import org.example.shked.Groups.DAL.entities.Schedule;
import org.example.shked.Groups.DAL.entities.TeacherSchedule;
import org.example.shked.Groups.web.DTOs.GroupListDTO.GroupDTO;
import org.example.shked.Groups.web.DTOs.groupDTOs.ScheduleDTO;
import org.example.shked.Groups.web.DTOs.lecturerDTOs.TeacherScheduleDTO;
import org.example.shked.Groups.service.mapper.GroupListMapper;
import org.example.shked.Groups.service.mapper.ScheduleGroupMapper;
import org.example.shked.Groups.service.mapper.ScheduleLecturerMapper;
import org.example.shked.Groups.DAL.repositories.GroupScheduleRepository;
import org.example.shked.Groups.DAL.repositories.GroupsRepository;
import org.example.shked.Groups.DAL.repositories.TeacherScheduleRepository;
import org.example.shked.Groups.service.sources.ScheduleApiService;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleApiService scheduleApiService;
    private final GroupScheduleRepository groupScheduleRepository;
    private final TeacherScheduleRepository teacherScheduleRepository;
    private final GroupsRepository groupsRepository;

    private static final int CACHE_TIME = 6;

    public ScheduleService(ScheduleApiService scheduleApiService, GroupScheduleRepository groupScheduleRepository, TeacherScheduleRepository teacherScheduleRepository, GroupsRepository groupsRepository) {
        this.scheduleApiService = scheduleApiService;
        this.groupScheduleRepository = groupScheduleRepository;
        this.teacherScheduleRepository = teacherScheduleRepository;
        this.groupsRepository = groupsRepository;
    }


    @Transactional
    public ScheduleDTO getGroupSchedule(String groupName) {
        LocalDateTime cachedTime = LocalDateTime.now().minusHours(CACHE_TIME);

        // БД если меньше CACHE_TIME
        Optional<Schedule> cachedSchedule = groupScheduleRepository.findByGroupNameAndUpdatedAtAfter(groupName, cachedTime);
        if (cachedSchedule.isPresent()) {
            return ScheduleGroupMapper.toDTO(cachedSchedule.get());
        }

        // Если больше CACHE_TIME, то берем с API
        Schedule updatedSchedule = scheduleApiService.getGroupSchedule(groupName);
        if (updatedSchedule != null) {
            updatedSchedule.setUpdatedAt(LocalDateTime.now());

            // Проверка на существование записи с данным groupName
            Optional<Schedule> existingSchedule = groupScheduleRepository.findByGroupName(groupName);
            // Обновление существующей записи
            // сохраняем старый ID для обновления
            existingSchedule.ifPresent(schedule -> updatedSchedule.setId(schedule.getId()));

            groupScheduleRepository.save(updatedSchedule);
            return ScheduleGroupMapper.toDTO(updatedSchedule);
        }

        // Если API недоступно, берем старые данные из БД
        Optional<Schedule> oldSchedule = groupScheduleRepository.findByGroupName(groupName);
        if (oldSchedule.isPresent()) {
            return ScheduleGroupMapper.toDTO(oldSchedule.get());
        }

        throw new RuntimeException("Расписание не найдено");
    }



    @Transactional
    public TeacherScheduleDTO getTeacherSchedule(String teacherId) {
        LocalDateTime cachedTime = LocalDateTime.now().minusHours(CACHE_TIME);

        // 1. Кэш
        Optional<TeacherSchedule> cached = teacherScheduleRepository
                .findByTeacherIdAndUpdatedAtAfter(teacherId, cachedTime);
        if (cached.isPresent()) {
            return ScheduleLecturerMapper.toDTO(cached.get());
        }

        // 2. Получение из API
        TeacherSchedule updated = scheduleApiService.getTeacherSchedule(teacherId);
        if (updated == null) {
            // 3. Fallback
            return teacherScheduleRepository.findByTeacherId(teacherId)
                    .map(ScheduleLecturerMapper::toDTO)
                    .orElseThrow(() -> new RuntimeException("Расписание преподавателя не найдено"));
        }

        LocalDateTime now = LocalDateTime.now();
        updated.setUpdatedAt(now);

        // 4. Привязка id существующего объекта, если найден
        teacherScheduleRepository.findByTeacherId(teacherId)
                .ifPresent(existing -> updated.setId(existing.getId()));

        // 5. Установка обратных связей
        updated.getDailySchedules().forEach(daily -> {
            daily.setTeacherSchedule(updated);
            daily.getLessons().forEach(lesson -> lesson.setDailySchedule(daily));
        });

        // 6. Сохранение каскадом
        TeacherSchedule saved = teacherScheduleRepository.save(updated);

        return ScheduleLecturerMapper.toDTO(saved);
    }




    @Transactional
    public List<GroupDTO> getAllGroups() {
        LocalDateTime cachedTime = LocalDateTime.now().minusHours(CACHE_TIME);

        // 1. Кэш
        List<Group> cachedGroups = groupsRepository.findByUpdatedAtAfter(cachedTime);
        if (!cachedGroups.isEmpty()) {
            return cachedGroups.stream().map(GroupListMapper::toDTO).toList();
        }

        // 2. Обновление из API
        List<Group> updatedGroups = scheduleApiService.getGroupList();
        if (!updatedGroups.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            List<Group> existingGroups = groupsRepository.findAll();

            // Создание карты существующих групп по ключу
            Map<String, Group> existingMap = existingGroups.stream()
                    .collect(Collectors.toMap(this::groupKey, Function.identity()));

            Set<String> receivedKeys = new HashSet<>();
            List<Group> groupsToSave = new ArrayList<>();

            for (Group updated : updatedGroups) {
                String key = groupKey(updated);
                receivedKeys.add(key);

                if (existingMap.containsKey(key)) {
                    Group existing = existingMap.get(key);
                    existing.setUpdatedAt(now);
                    groupsToSave.add(existing);
                } else {
                    updated.setUpdatedAt(now);
                    groupsToSave.add(updated);
                }
            }

            // Удаляем только те группы, которых нет среди полученных
            List<Group> groupsToDelete = existingGroups.stream()
                    .filter(g -> !receivedKeys.contains(groupKey(g)))
                    .toList();

            groupsRepository.deleteAll(groupsToDelete);
            groupsRepository.saveAll(groupsToSave);

            return groupsToSave.stream().map(GroupListMapper::toDTO).toList();
        }

        // 3. Fallback
        List<Group> fallback = groupsRepository.findAll();
        if (!fallback.isEmpty()) {
            return fallback.stream().map(GroupListMapper::toDTO).toList();
        }

        throw new RuntimeException("Список групп не найден");
    }

    private String groupKey(Group g) {
        return String.join("_", g.getName(), g.getFac(), g.getLevel(), g.getCourse());
    }



}
