package com.app.models;

import java.util.ArrayList;
import java.util.List;

public class Period {
    private Date date;
    private Time time;
    private PeriodType periodType;
    private String notes;
    private final List<User> collaborators = new ArrayList<User>();

    public Period(Date date, Time time, PeriodType periodType, String notes) {
        this.date = date;
        this.time = time;
        this.periodType = periodType;
        this.notes = notes;
    }

    public void addCollaborators(User... users) {
        collaborators.addAll(List.of(users));
    }

    public void removeCollaborators(User... users) {
        for (User user : users) {
            collaborators.remove(user);
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
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
}
