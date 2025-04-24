package org.example.shked.Groups.DAL.enums;

public enum LessonType {
    LECTURE("ЛК", "lecture"),
    PRACTICAL("ПЗ", "practical"),
    LABORATORY("ЛР", "laboratory"),
    EXAM("Экзамен", "exam"),
    UNKNOWN("", "unknown");

    private final String code;
    private final String englishName;

    LessonType(String code, String englishName) {
        this.code = code;
        this.englishName = englishName;
    }

    public String getCode() {
        return code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public static LessonType fromCode(String code) {
        for (LessonType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
