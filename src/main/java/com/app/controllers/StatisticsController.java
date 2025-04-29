package com.app.controllers;

import com.app.models.Database;
import com.app.models.PeriodType;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

public class StatisticsController {

    @FXML
    public VBox objectivesVBox;

    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label pauseCountLabel;
    @FXML
    private Label goalsReachedLabel;

    public void initialize() {
        objectivesVBox.getChildren().clear();

        int pauseCount = 0;
        double totalHours = 0;
        double totalMinutes = 0;
        int objectivesCompleted = 0;
        List<PeriodType> periodTypes = Database.getPeriodTypesOfUser(Database.getConnectedUser());

        for (PeriodType periodType : periodTypes) {
            Label objectiveTitleLabel = new Label();
            ProgressBar progressBar = new ProgressBar(0);
            Label objectiveLabel = new Label();

            long completedSeconds = periodType.getCompletedTimeObjective().getSeconds();
            long totalSeconds = periodType.getTimeObjective().getSeconds();

            long completedHours = completedSeconds / 3600;
            long completedMinutes = (completedSeconds % 3600) / 60;

            long objectiveHours = totalSeconds / 3600;
            long objectiveMinutes = (totalSeconds % 3600) / 60;

            double completedObjective = (double) completedSeconds / 3600;

            pauseCount += periodType.getPauseContainer().getCount();
            totalHours += completedObjective;
            totalMinutes += (completedSeconds % 3600) / 60.0;

            if (periodType.objectiveIsCompleted()) objectivesCompleted++;

            objectiveTitleLabel.setText(
                    String.format("Objectif hebdomadaire : %s (%dh %02dmin)",
                            periodType.getTitle(),
                            objectiveHours,
                            objectiveMinutes)
            );

            progressBar.getStyleClass().add("progress-bar");

            if (periodType.objectiveIsCompleted()) {
                progressBar.setStyle("-fx-accent: gold;");
            } else {
                progressBar.setStyle("-fx-accent: #4CAF50;");
            }

            animateProgressBar(progressBar, totalSeconds > 0 ? (double) completedSeconds / totalSeconds : 1);

            objectiveLabel.setText(String.format("%dh %02dmin / %dh %02dmin",
                    completedHours, completedMinutes, objectiveHours, objectiveMinutes));
            objectiveLabel.getStyleClass().add("progress-value");

            VBox vbox = new VBox(objectiveTitleLabel, progressBar, objectiveLabel);
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(5);
            vbox.getStyleClass().add("objective-box");

            objectivesVBox.getChildren().add(vbox);
        }

        double totalExactHours = totalHours + (totalMinutes / 60);
        long displayHours = (long) totalExactHours;
        long displayMinutes = Math.round((totalExactHours - displayHours) * 60);

        totalTimeLabel.setText(String.format("%dh %02dmin", displayHours, displayMinutes));
        pauseCountLabel.setText(String.valueOf(pauseCount));
        goalsReachedLabel.setText(String.format("%d / %d", objectivesCompleted, periodTypes.size()));
    }

    @FXML
    public void onExportClicked() {
        System.out.println("Exportation des statistiques"); // À remplacer par vraie exportation si souhaité
    }

    @FXML
    public void onResetClicked() {
        // Créer une boîte de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Réinitialiser les statistiques");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir tout remettre à zéro ? Cette action est irréversible.");

        // Attendre la réponse de l'utilisateur
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Si OK, réinitialiser
                for (PeriodType periodType : Database.getPeriodTypesOfUser(Database.getConnectedUser())) {
                    periodType.resetCompletedObjective();
                }
                initialize();
            }
            // Si annulé, rien faire
        });
    }

    // Animation douce du remplissage des barres de progression
    private void animateProgressBar(ProgressBar progressBar, double finalValue) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(progressBar.progressProperty(), finalValue))
        );
        timeline.play();
    }
}
