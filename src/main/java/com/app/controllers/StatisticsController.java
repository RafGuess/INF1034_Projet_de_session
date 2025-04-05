package com.app.controllers;

import com.app.models.Database;
import com.app.models.Period;
import com.app.models.PeriodType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.List;

public class StatisticsController {

    // Barres de progression et étiquettes liées à l'étude
    @FXML private ProgressBar studyProgress;
    @FXML private Label studyLabel;

    // Barres de progression et étiquettes liées au travail
    @FXML private ProgressBar workProgress;
    @FXML private Label workLabel;

    // Étiquette pour le temps total d'activités
    @FXML private Label totalTimeLabel;

    // Étiquette pour le nombre de pauses prises
    @FXML private Label pauseCountLabel;

    public void initialize() {
        // Récupère toutes les périodes de l'utilisateur connecté
        List<Period> periods = Database.getPeriodsOfUser(Database.getConnectedUser());

        double studyHours = 0; // compteur d'heures d'étude
        double workHours = 0;  // compteur d'heures de travail
        int pauseCount = 0;    // compteur de pauses

        // Parcourt toutes les périodes
        for (Period p : periods) {
            // Calcule la durée de la période en heures
            double duration = p.getDuration().toMinutes() / 60.0;

            // Catégorise selon le type de période
            if (p.getPeriodType().getTitle().equalsIgnoreCase("Étude")) {
                studyHours += duration;
            } else if (p.getPeriodType().getTitle().equalsIgnoreCase("Travail")) {
                workHours += duration;
            }

            // Ajoute le nombre de pauses (à implémenter dans le modèle si absent)
            pauseCount += p.getPauseCount();
        }

        // Objectifs hebdomadaires fixés (en heures)
        double studyGoal = 15.0;
        double workGoal = 20.0;

        // Met à jour la progression visuelle, sans dépasser 100%
        studyProgress.setProgress(Math.min(1.0, studyHours / studyGoal));
        workProgress.setProgress(Math.min(1.0, workHours / workGoal));

        // Affiche les heures atteintes vs objectifs
        studyLabel.setText(String.format("Étude : %.1f / %.1f h", studyHours, studyGoal));
        workLabel.setText(String.format("Travail : %.1f / %.1f h", workHours, workGoal));

        // Affiche le temps total combiné
        totalTimeLabel.setText(String.format("Temps total : %.1f h", studyHours + workHours));

        // Affiche le nombre de pauses
        pauseCountLabel.setText("Pauses prises : " + pauseCount);
    }
}
