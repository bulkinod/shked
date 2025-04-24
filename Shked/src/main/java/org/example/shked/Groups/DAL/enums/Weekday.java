package org.example.shked.Groups.DAL.enums;

import java.util.Arrays;

public enum Weekday {
    MONDAY("Пн", 1),
    TUESDAY("Вт", 2),
    WEDNESDAY("Ср", 3),
    THURSDAY("Чт", 4),
    FRIDAY("Пт", 5),
    SATURDAY("Сб", 6),
    SUNDAY("Вс", 7);

    private final String shortName;
    private final int dayNumber;

    Weekday(String shortName, int dayNumber) {
        this.shortName = shortName;
        this.dayNumber = dayNumber;
    }

    public String getShortName() {
        return shortName;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public static Weekday fromShortName(String name) {
        return Arrays.stream(values())
                .filter(day -> day.shortName.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown weekday: " + name));
    }
}
