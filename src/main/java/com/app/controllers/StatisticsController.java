package com.app.controllers;

import com.app.models.Database;
import com.app.models.Period;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.List;

public class StatisticsController {

    // Barres de progression et étiquettes associées pour l’étude
    @FXML private ProgressBar studyProgress;
    @FXML private Label studyLabel;

    // Barres de progression et étiquettes associées pour le travail
    @FXML private ProgressBar workProgress;
    @FXML private Label workLabel;

    // Barres de progression et étiquettes associées pour l’activité physique
    @FXML private ProgressBar physicalProgress;
    @FXML private Label physicalLabel;

    // Résumés : temps total, pauses, objectifs atteints
    @FXML private Label totalTimeLabel;
    @FXML private Label pauseCountLabel;
    @FXML private Label goalsReachedLabel;

    // Initialisation de la vue avec calcul des statistiques
    public void initialize() {
        System.out.println("initialize() - Statistiques"); // debug console

        // Récupère toutes les périodes de l’utilisateur connecté
        List<Period> periods = Database.getPeriodsOfUser(Database.getConnectedUser());

        // Compteurs pour chaque catégorie
        double studyHours = 0;
        double workHours = 0;
        double physicalHours = 0;
        int pauseCount = 0;

        // Parcourt chaque période
        for (Period p : periods) {
            double duration = p.getDuration().toMinutes() / 60.0; // conversion en heures
            String title = p.getPeriodType().getTitle().toLowerCase(); // normalisation du titre

            // Classement de la période selon son type
            switch (title) {
                case "étude" -> studyHours += duration;
                case "travail" -> workHours += duration;
                case "activité physique", "sport" -> physicalHours += duration;
            }

            // Compte les pauses
            pauseCount += p.getPauseCount();
        }

        // Objectifs prédéfinis
        double studyGoal = 15.0, workGoal = 20.0, physicalGoal = 5.0;

        // Compte le nombre d’objectifs atteints
        int goalsReached = 0;
        if (studyHours >= studyGoal) goalsReached++;
        if (workHours >= workGoal) goalsReached++;
        if (physicalHours >= physicalGoal) goalsReached++;

        // Met à jour les barres de progression (max à 1.0)
        studyProgress.setProgress(Math.min(1.0, studyHours / studyGoal));
        workProgress.setProgress(Math.min(1.0, workHours / workGoal));
        physicalProgress.setProgress(Math.min(1.0, physicalHours / physicalGoal));

        // Met à jour les labels d’avancement
        studyLabel.setText(String.format("%.0f h / %.0f h", studyHours, studyGoal));
        workLabel.setText(String.format("%.0f h / %.0f h", workHours, workGoal));
        physicalLabel.setText(String.format("%.0f h / %.0f h", physicalHours, physicalGoal));

        // Met à jour les résumés en bas de page
        totalTimeLabel.setText(String.format("%.0f h", studyHours + workHours + physicalHours));
        pauseCountLabel.setText(String.valueOf(pauseCount));
        goalsReachedLabel.setText(String.format("%d / 3", goalsReached));
    }

    // Action : exportation des données (à implémenter)
    @FXML
    public void onExportClicked() {
        System.out.println("Exportation des statistiques"); // à remplacer par logique d'export
    }

    // Action : réinitialisation des statistiques (à implémenter)
    @FXML
    public void onResetClicked() {
        System.out.println("Réinitialisation des statistiques"); // à remplacer par reset réel
    }
}
