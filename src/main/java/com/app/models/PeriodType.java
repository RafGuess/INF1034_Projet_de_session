package com.app.models;

import com.app.utils.LocalDateUtils;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDate;

public class PeriodType {

    // Nom du type de période (ex : Étude, Travail)
    private String title;

    // Couleur associée à ce type (utilisée pour l'affichage)
    private Color color;

    // Objectif de temps hebdomadaire pour ce type
    private Duration timeObjective;

    // Temps déjà accompli pour cet objectif
    private Duration completedTimeObjective = Duration.ZERO;

    // Date de la dernière mise à jour du temps accompli
    private LocalDate lastUpdated = LocalDate.now();

    // Nombre de pauses prises pendant cette période
    private int pauseCount = 0;

    // Constructeur
    public PeriodType(String title, Color color, Duration timeObjective) {
        this.title = title;
        this.color = color;
        this.timeObjective = timeObjective;
    }

    // Getters et setters de base

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

    // Convertit la couleur en format hexadécimal HTML (ex : #00ff99)
    public String getRGBColor() {
        return String.format("#%02x%02x%02x",
                (int)(color.getRed()*255),
                (int)(color.getGreen()*255),
                (int)(color.getBlue()*255));
    }

    // Retourne le nombre de pauses prises
    public int getPauseCount() {
        return pauseCount;
    }

    // Incrémente le nombre de pauses prises
    public void incrementPauseCount() {
        this.pauseCount++;
    }

    // Définit un nouvel objectif de temps et réinitialise l'accompli
    public void setTimeObjective(Duration timeObjective) {
        this.timeObjective = timeObjective;
        completedTimeObjective = Duration.ZERO;
    }

    public Duration getTimeObjective() {
        return timeObjective;
    }

    // Retourne le temps accompli, après vérification de validité temporelle
    public Duration getCompletedTimeObjective() {
        resetObjectiveIfOutdated();
        return completedTimeObjective;
    }

    // Réinitialise le compteur si la semaine a changé
    private void resetObjectiveIfOutdated() {
        if (lastUpdated.isBefore(LocalDateUtils.getFirstDayOfWeek(LocalDate.now()))) {
            resetCompletedObjective();
        }
    }

    // Réinitialise le compteur
    public void resetCompletedObjective() {
        completedTimeObjective = Duration.ZERO;
        lastUpdated = LocalDate.now();
    }

    // Ajoute du temps accompli à l'objectif, sans dépasser la limite
    public void updateCompletedTimeObjective(Duration timeToAdd) {
        resetObjectiveIfOutdated();

        Duration newCompletedTimeObjective = completedTimeObjective.plus(timeToAdd);

        // Limite la progression à l'objectif maximal
        if (newCompletedTimeObjective.minus(timeObjective).isPositive()) {
            completedTimeObjective = timeObjective;
        } else {
            completedTimeObjective = newCompletedTimeObjective;
        }

        lastUpdated = LocalDate.now(); // mise à jour de la date
    }

    // Vérifie que l'objectif est complété pour la semaine
    public boolean objectiveIsCompleted() {
        return completedTimeObjective.equals(timeObjective);
    }

    // Utilisé pour l'affichage dans les ComboBox
    @Override
    public String toString() {
        return title;
    }
}
