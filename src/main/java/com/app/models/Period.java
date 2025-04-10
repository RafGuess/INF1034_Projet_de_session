package com.app.models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Period {

    // Date à laquelle la période a lieu
    private LocalDate date;

    // Heure de début de la période
    private LocalTime startTime;

    // Heure de fin de la période
    private LocalTime endTime;

    // Type de la période (ex : Étude, Travail, etc.)
    private PeriodType periodType;

    // Notes associées à cette période (ex : remarques personnelles)
    private String notes;

    // Liste des collaborateurs participant à cette période
    private final List<User> collaborators = new ArrayList<>();

    // Nombre de pauses prises pendant cette période
    private int pauseCount = 0;

    // Constructeur principal
    public Period(
            LocalDate date, LocalTime startTime, LocalTime endTime,
            PeriodType periodType, String notes, List<User> collaborators
    ) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.periodType = periodType;
        this.notes = notes;
        this.collaborators.addAll(collaborators); // copie des collaborateurs passés en argument
    }

    // Getters et setters

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

    // Calcule la durée entre le début et la fin
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

    // Retourne la liste des collaborateurs
    public List<User> getCollaborators() {
        return collaborators;
    }

    // Retourne le nombre de pauses prises
    public int getPauseCount() {
        return pauseCount;
    }

    // Incrémente le nombre de pauses prises
    public void incrementPauseCount() {
        this.pauseCount++;
    }

    @Override
    public String toString() {
        return String.format("Period(date=%s, startTime=%s, endTime=%s, periodType=%s, notes=%s, collaborators=%s)"
                , date, startTime, endTime, periodType, notes, collaborators);
    }
}
