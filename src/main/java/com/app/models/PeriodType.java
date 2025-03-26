package com.app.models;

import com.app.utils.LocalDateUtils;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDate;

public class PeriodType {
    private String title;
    private Color color;
    private Duration timeObjective;
    private Duration completedTimeObjective = Duration.ZERO;
    private LocalDate lastUpdated = LocalDate.now();

    public PeriodType(String title, Color color, Duration timeObjective) {
        this.title = title;
        this.color = color;
        this.timeObjective = timeObjective;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getRGBColor() {
        return String.format("#%02x%02x%02x", (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
    }

    public void setTimeObjective(Duration timeObjective) {
        this.timeObjective = timeObjective;
        completedTimeObjective = Duration.ZERO;
    }

    public Duration getTimeObjective() {
        return timeObjective;
    }

    public Duration getCompletedTimeObjective() {
        resetObjectiveIfOutdated();
        return completedTimeObjective;
    }

    private void resetObjectiveIfOutdated() {
        if (lastUpdated.isBefore(LocalDateUtils.getFirstDayOfWeek(LocalDate.now()))) {
            completedTimeObjective = Duration.ZERO;
            lastUpdated = LocalDate.now();
        }
    }

    public void updateCompletedTimeObjective(Duration timeToAdd) {
        resetObjectiveIfOutdated();

        Duration newCompletedTimeObjective = completedTimeObjective.plus(timeToAdd);
        if (newCompletedTimeObjective.minus(timeObjective).isPositive()) {
            completedTimeObjective = timeObjective;
        } else {
            completedTimeObjective = newCompletedTimeObjective;
        }
        lastUpdated = LocalDate.now();
    }

    @Override
    public String toString() {
        return title;
    }
}
