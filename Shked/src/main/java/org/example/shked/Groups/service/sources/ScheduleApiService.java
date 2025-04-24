package org.example.shked.Groups.service.sources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.shked.Groups.DAL.entities.*;
import org.example.shked.Groups.DAL.enums.LessonType;
import org.example.shked.Groups.DAL.enums.Weekday;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ScheduleApiService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss");

    @Value("${schedule-api.base-url}")
    private String baseUrl;

    @Value("${schedule-api.schedules-collection}")
    private String schedulesCollection;

    public ScheduleApiService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public Schedule getGroupSchedule(String groupName) {
        groupName = hashMd5(groupName);
        String url = baseUrl + schedulesCollection + groupName + ".json";

        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseGroupSchedule(response);
        } catch (Exception e) {
            return null;
        }
    }

    public TeacherSchedule getTeacherSchedule(String teacherUid) {
        String url = baseUrl + schedulesCollection + teacherUid + ".json";

        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseTeacherSchedule(response, teacherUid);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Group> getGroupList() {
        String url = baseUrl + schedulesCollection + "groups.json";

        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseGroups(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    private Schedule parseGroupSchedule(String response) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(response);
        Schedule schedule = new Schedule();
        schedule.setGroupName(jsonNode.get("group").asText());

        for (Iterator<String> key = jsonNode.fieldNames(); key.hasNext(); ) {
            String date = key.next();

            if (date.equals("group")) {
                continue;
            }

            JsonNode dayNode = jsonNode.get(date);
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            DailySchedule dailySchedule = new DailySchedule();
            Weekday weekday = Weekday.fromShortName(dayNode.get("day").asText());
            dailySchedule.setWeekday(weekday);
            dailySchedule.setDate(localDate);
            dailySchedule.setSchedule(schedule);

            JsonNode pairsNode = dayNode.get("pairs");
            if (pairsNode != null) {
                for (Iterator<String> timeIterator = pairsNode.fieldNames(); timeIterator.hasNext(); ) {
                    String startTime = timeIterator.next();
                    JsonNode lessonNode = pairsNode.get(startTime);

                    for (Iterator<String> subjectIterator = lessonNode.fieldNames(); subjectIterator.hasNext(); ) {
                        String subjectName = subjectIterator.next();
                        JsonNode lessonDetails = lessonNode.get(subjectName);

                        int ordinal = getOrdinalFromStartTime(startTime);
                        String typeCode = lessonDetails.get("type").fieldNames().next();
                        LessonType type = LessonType.fromCode(typeCode);

                        Map.Entry<String, JsonNode> roomEntry = lessonDetails.get("room").fields().next();
                        Map.Entry<String, JsonNode> lecturerEntry = lessonDetails.get("lector").fields().next();

                        String locationId = roomEntry.getKey();
                        String location = roomEntry.getValue().asText();

                        String lecturerId = lecturerEntry.getKey();
                        String lecturer = lecturerEntry.getValue().asText();

                        Lesson lesson = new Lesson();
                        lesson.setName(subjectName);
                        lesson.setOrdinal(ordinal);
                        lesson.setType(type);
                        lesson.setLocation(location);
                        lesson.setLocationId(locationId);
                        lesson.setLecturer(lecturer);
                        lesson.setLecturerId(lecturerId);
                        lesson.setStartTime(LocalTime.parse(lessonDetails.get("time_start").asText(), TIME_FORMATTER));
                        lesson.setEndTime(LocalTime.parse(lessonDetails.get("time_end").asText(), TIME_FORMATTER));
                        lesson.setDailySchedule(dailySchedule);

                        dailySchedule.getLessons().add(lesson);
                    }
                }
            }

            schedule.getDailySchedules().add(dailySchedule);
        }
        return schedule;
    }

    private TeacherSchedule  parseTeacherSchedule(String response, String teacherUid) throws JsonProcessingException {
        JsonNode json = objectMapper.readTree(response);

        TeacherSchedule teacherSchedule = new TeacherSchedule();
        teacherSchedule.setTeacherName(json.get("name").asText());
        teacherSchedule.setTeacherId(teacherUid);

        JsonNode scheduleNode = json.get("schedule");
        for (Iterator<String> it = scheduleNode.fieldNames(); it.hasNext(); ) {
            String dateStr = it.next();
            JsonNode dayNode = scheduleNode.get(dateStr);
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            DailySchedule dailySchedule = new DailySchedule();
            dailySchedule.setDate(date);
            Weekday weekday = Weekday.fromShortName(dayNode.get("day").asText());
            dailySchedule.setWeekday(weekday);
            dailySchedule.setTeacherSchedule(teacherSchedule);

            JsonNode pairsNode = dayNode.get("pairs");
            for (Iterator<String> times = pairsNode.fieldNames(); times.hasNext(); ) {
                String startTime = times.next();
                JsonNode lessonNode = pairsNode.get(startTime);

                Lesson lesson = new Lesson();
                lesson.setStartTime(LocalTime.parse(lessonNode.get("time_start").asText(), TIME_FORMATTER));
                lesson.setEndTime(LocalTime.parse(lessonNode.get("time_end").asText(), TIME_FORMATTER));
                lesson.setName(lessonNode.get("name").asText());
                lesson.setOrdinal(getOrdinalFromStartTime(startTime));

                // Тип занятия
                JsonNode typesNode = lessonNode.get("types");
                if (typesNode != null && typesNode.isArray() && typesNode.size() > 0) {
                    lesson.setType(LessonType.fromCode(typesNode.get(0).asText()));
                }

                // Аудитория
                JsonNode roomsNode = lessonNode.get("rooms");
                if (roomsNode != null && roomsNode.fieldNames().hasNext()) {
                    Map.Entry<String, JsonNode> room = roomsNode.fields().next();
                    lesson.setLocationId(room.getKey());
                    lesson.setLocation(room.getValue().asText());
                }

                JsonNode groupsNode = lessonNode.get("groups");
                if (groupsNode != null && groupsNode.isArray()) {
                    Set<String> groupSet = new HashSet<>();
                    for (JsonNode group : groupsNode) {
                        groupSet.add(group.asText());
                    }
                    lesson.setGroupNames(groupSet);
                }

                lesson.setDailySchedule(dailySchedule);
                dailySchedule.getLessons().add(lesson);
            }

            teacherSchedule.getDailySchedules().add(dailySchedule);
        }

        return teacherSchedule;
    }

    private List<Group> parseGroups (String response) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(response);
        List<Group> groups = new ArrayList<>();

        for (JsonNode node : root) {
            Group group = new Group();
            group.setName(node.get("name").asText());
            group.setFac(node.get("fac").asText());
            group.setLevel(node.get("level").asText());
            group.setCourse(node.get("course").asText());
            groups.add(group);
        }

        return groups;
    }


    private int getOrdinalFromStartTime(String time) {
        switch (time) {
            case "9:00:00": return 1;
            case "10:45:00": return 2;
            case "13:00:00": return 3;
            case "14:45:00": return 4;
            case "16:30:00": return 5;
            case "18:15:00": return 6;
            case "20:00:00": return 7;
            default: throw new IllegalArgumentException("Invalid class time: " + time);
        }
    }

    private String hashMd5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
