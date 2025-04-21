package com.app.models;

import com.app.utils.LocalDateUtils;

import java.time.Duration;
import java.time.LocalDate;

public class PauseContainer {

    // Durée et fréquence associée à un type de période
    private Duration length = Duration.ZERO;
    private Duration frequency = Duration.ZERO;

    // Nombre de pauses prises pendant la semaine pour ce type de periode
    private int count = 0;

    // Dernière modification de pauseCount
    LocalDate lastUpdated = LocalDate.now();

    // Getters et setters pour la longueur et la fréquence de la période
    public void setLength(Duration length) {
        this.length = length;
    }

    public Duration getLength() {
        return length;
    }

    public void setFrequency(Duration frequency) {
        this.frequency = frequency;
    }

    public Duration getFrequency() {
        return frequency;
    }

    // Retourne le nombre de pauses prises
    public int getCount() {
        resetPauseCountIfOutdated();
        return count;
    }

    // Réinitialise le compteur si la semaine a changé
    private void resetPauseCountIfOutdated() {
        if (lastUpdated.isBefore(LocalDateUtils.getFirstDayOfWeek(LocalDate.now()))) {
            resetPauseCount();
        }
    }

    // Réinitialise le compteur
    public void resetPauseCount() {
        count = 0;
        lastUpdated = LocalDate.now();
    }

    // Officialise la pause en incrémentant le compteur
    public void takePause() {
        resetPauseCountIfOutdated();
        count++;
    }
}
