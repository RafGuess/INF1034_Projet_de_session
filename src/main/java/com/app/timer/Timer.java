package com.app.timer;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {

    // Propriété contenant le nombre de secondes écoulées (commence à -1)
    private static final SimpleIntegerProperty seconds = new SimpleIntegerProperty(0);

    // Service qui exécute le chronomètre en tâche planifiée
    private static ScheduledExecutorService chronometerExecutor;

    // Démarre le minuteur si ce n’est pas déjà fait
    public static void startTimer() {
        // Vérifie que le timer ne tourne pas déjà
        if (chronometerExecutor != null && !chronometerExecutor.isShutdown()) {
            return;
        }

        // Crée un exécuteur qui incrémente chaque seconde
        chronometerExecutor = Executors.newSingleThreadScheduledExecutor();
        chronometerExecutor.scheduleAtFixedRate(Timer::incrementTime, 1, 1, TimeUnit.SECONDS);
    }

    // Incrémente le nombre de secondes
    private static void incrementTime() {
        seconds.set(seconds.get() + 1);
    }

    // Ajoute un écouteur pour réagir aux changements de temps
    public static void addListener(TimerListener listener) {
        ChangeListener<Number> castedListener = listener::changedTimer;
        seconds.addListener(castedListener);
    }

    // Retire un écouteur
    public static void removeListener(TimerListener listener) {
        ChangeListener<Number> castedListener = listener::changedTimer;
        seconds.removeListener(castedListener);
    }

    // Arrête le minuteur en cours
    public static void stopTimer() {
        if (chronometerExecutor != null) {
            chronometerExecutor.shutdown(); // stoppe l'exécution du timer
        }
    }

    // Réinitialise le minuteur à 0 seconde et l'arrête
    public static void resetTimer() {
        stopTimer();           // arrête le thread
        seconds.set(0);       // réinitialise le compteur
    }
}
