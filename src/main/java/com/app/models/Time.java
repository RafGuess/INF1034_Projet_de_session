package com.app.models;

import java.time.LocalTime;

public class Time {
    private LocalTime time;

    public Time(LocalTime time) {
        this.time = time;
    }

    public Time() {
        this.time = LocalTime.now();
    }

    public static Time getMidnight() {
        return new Time(LocalTime.MIDNIGHT);
    }

    public void addHours(int numHours) {
        time = time.plusHours(numHours);
    }

    public void removeHours(int numHours) {
        time = time.minusHours(numHours);
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

}
