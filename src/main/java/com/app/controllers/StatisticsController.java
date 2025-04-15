package com.app.controllers;

import com.app.models.Database;
import com.app.models.PeriodType;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.util.List;

public class StatisticsController {

    // VBox contenant tous les objectifs ajoutés par le controller après analyse du model
    @FXML public VBox objectivesVBox;

    // Résumés : temps total, pauses, objectifs atteints
    @FXML private Label totalTimeLabel;
    @FXML private Label pauseCountLabel;
    @FXML private Label goalsReachedLabel;

    // Initialisation de la vue avec calcul des statistiques
    public void initialize() {
        objectivesVBox.getChildren().clear();

        int pauseCount = 0;
        double totalHours = 0;
        int objectivesCompleted = 0;
        List<PeriodType> periodTypes = Database.getPeriodTypesOfUser(Database.getConnectedUser());

        for (PeriodType periodType : periodTypes) {
            // Création des trois éléments descriptifs de l'objectif
            Label objectiveTitleLabel = new Label();
            ProgressBar progressBar = new ProgressBar();
            Label objectiveLabel = new Label();

            // Tire les informations nécessaires du model
            double completedObjective = (double) periodType.getCompletedTimeObjective().getSeconds() /3600;
            double objective = (double) periodType.getTimeObjective().getSeconds() /3600;
            pauseCount += periodType.getPauseCount();
            totalHours += completedObjective;
            if (periodType.objectiveIsCompleted()) objectivesCompleted++;

            // MàJ les éléments descriptifs de l'objectif
            objectiveTitleLabel.setText(
                    String.format("Objectif hebdomadaire : %s (%dh)",
                            periodType.getTitle(),
                            periodType.getTimeObjective().toHours())
            );
            progressBar.setProgress(completedObjective/objective);
            progressBar.getStyleClass().add("progress-bar");
            objectiveLabel.setText(String.format("%.0f h / %.0f h", completedObjective, objective));
            objectiveLabel.getStyleClass().add("progress-value");

            // Ajout des éléments à l'interface
            VBox vbox = new VBox(objectiveTitleLabel, progressBar, objectiveLabel);
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(5);
            objectivesVBox.getChildren().add(vbox);
        }

        // Met à jour les résumés en bas de page
        totalTimeLabel.setText(String.format("%.0f h", totalHours));
        pauseCountLabel.setText(String.valueOf(pauseCount));
        goalsReachedLabel.setText(String.format("%d / %d", objectivesCompleted, periodTypes.size()));
    }

    // Action : exportation des données (à implémenter)
    @FXML
    public void onExportClicked() {
        System.out.println("Exportation des statistiques"); // à remplacer par logique d'export
    }

    // Action : réinitialisation des statistiques (à implémenter)
    @FXML
    public void onResetClicked() {
        for (PeriodType periodType : Database.getPeriodTypesOfUser(Database.getConnectedUser())) {
            periodType.resetCompletedObjective();
        }
        initialize();
    }
}
