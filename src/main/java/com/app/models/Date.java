package com.app.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Date {
    private LocalDate date;
    private static final DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH);
    private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH);

    public Date(LocalDate date) {
        this.date = date;
    }

    public Date() {
        this.date = LocalDate.now();
    }

    public static Date getFirstDayOfWeek() {
        Date date = new Date();
        while (date.getDateOfWeek() != DayOfWeek.SUNDAY) {
            date.previousDay();
        }
        return date;
    }

    public LocalDate getLocalDate() {
        return date;
    }

    public void nextDay() {
        date = date.plusDays(1);
    }

    public void previousDay() {
        date = date.minusDays(1);
    }

    public DayOfWeek getDateOfWeek() {
        return date.getDayOfWeek();
    }

    @Override
    public String toString() {
        String dayOfTheWeek = date.format(dayOfWeekFormatter);
        String dayOfTheWeekCapitalized = dayOfTheWeek.substring(0,1).toUpperCase() + dayOfTheWeek.substring(1);
        String month = date.format(monthFormatter);

        return dayOfTheWeekCapitalized + "\n" +
                date.getDayOfMonth() + " " + month;
    }

}
