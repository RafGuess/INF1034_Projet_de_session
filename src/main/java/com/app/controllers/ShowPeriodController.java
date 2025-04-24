package com.app.controllers;

import com.app.controllers.controllerInterfaces.DataInitializable;
import com.app.models.Period;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.Duration;

public class ShowPeriodController implements DataInitializable<Period> {

    // Période à afficher dans l'interface
    private Period shownPeriod;

    @FXML private Label periodLabel;

    // Méthode appelée automatiquement lors du chargement du FXML avec des données
    @Override
    public void initializeWithData(Period period) {
        String stringBuilder = String.format("Type : %s\n", period.getPeriodType()) +
                String.format("Date : %s\n", period.getDate()) +
                String.format("Temps : %s - %s\n", period.getStartTime(), period.getEndTime()) +
                String.format("Collaborateurs : %s\n", period.getCollaborators()) +
                String.format("Notes : %s\n", period.getNotes()) +
                "\n" +
                String.format("Durée des pauses: %s\n", durationToString(period.getPeriodType().getPauseContainer().getLength())) +
                String.format("Fréquences des pauses: %s\n", durationToString(period.getPeriodType().getPauseContainer().getFrequency())) +
                String.format("Objectif hebdomadaire: %s\n", durationToString(period.getPeriodType().getTimeObjective())) +
                String.format("Objectif complété: %s\n", durationToString(period.getPeriodType().getCompletedTimeObjective()));

        shownPeriod = period; // Stocke la période reçue pour l'utiliser dans la vue
        periodLabel.setText(stringBuilder);
    }

    private String durationToString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02dh%02dm%02ds", hours, minutes, seconds);
    }
}
