package com.app.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateUtils {
    private static final DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH);
    private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH);

    private LocalDateUtils() {}

    public static LocalDate getFirstDayOfWeek(LocalDate date) {
        while (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }

    public static String formatForCalendar(LocalDate date) {
        String dayOfTheWeek = date.format(dayOfWeekFormatter);
        String dayOfTheWeekCapitalized = dayOfTheWeek.substring(0,1).toUpperCase() + dayOfTheWeek.substring(1);
        String month = date.format(monthFormatter);

        return dayOfTheWeekCapitalized + "\n" +
                date.getDayOfMonth() + " " + month;
    }
}
