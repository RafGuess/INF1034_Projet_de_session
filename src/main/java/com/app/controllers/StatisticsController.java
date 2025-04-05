package com.app.controllers;

import com.app.models.Database;
import com.app.models.Period;
import com.app.models.PeriodType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.List;

public class StatisticsController {

    @FXML private ProgressBar studyProgress;
    @FXML private Label studyLabel;
    @FXML private ProgressBar workProgress;
    @FXML private Label workLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Label pauseCountLabel;

    public void initialize() {
        List<Period> periods = Database.getPeriodsOfUser(Database.getConnectedUser());

        double studyHours = 0;
        double workHours = 0;
        int pauseCount = 0;

        for (Period p : periods) {
            double duration = p.getDuration().toMinutes() / 60.0;
            if (p.getPeriodType().getTitle().equalsIgnoreCase("Étude")) {
                studyHours += duration;
            } else if (p.getPeriodType().getTitle().equalsIgnoreCase("Travail")) {
                workHours += duration;
            }
            pauseCount += p.getPauseCount(); // à ajouter dans le modèle si non présent
        }

        double studyGoal = 15.0;
        double workGoal = 20.0;

        studyProgress.setProgress(Math.min(1.0, studyHours / studyGoal));
        workProgress.setProgress(Math.min(1.0, workHours / workGoal));

        studyLabel.setText(String.format("Étude : %.1f / %.1f h", studyHours, studyGoal));
        workLabel.setText(String.format("Travail : %.1f / %.1f h", workHours, workGoal));

        totalTimeLabel.setText(String.format("Temps total : %.1f h", studyHours + workHours));
        pauseCountLabel.setText("Pauses prises : " + pauseCount);
    }
}
