package com.app.models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Period {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private PeriodType periodType;
    private String notes;
    private final List<User> collaborators = new ArrayList<>();
    private int pauseCount = 0;

    public Period(
            LocalDate date, LocalTime startTime, LocalTime endTime,
            PeriodType periodType, String notes, List<User> collaborators
    ) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.periodType = periodType;
        this.notes = notes;
        this.collaborators.addAll(collaborators);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime time) {
        startTime = time;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime time) {
        endTime = time;
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<User> getCollaborators() {
        return collaborators;
    }

    public int getPauseCount() { return pauseCount; }

    public void incrementPauseCount() { this.pauseCount++; }
}
