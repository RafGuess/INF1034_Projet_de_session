package com.app.timer;

import com.app.AppManager;
import com.app.models.PauseContainer;
import com.app.models.Period;
import javafx.application.Platform;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PauseNotificationHandler implements InPeriodTimerListener {

    // Boolean qui vérifie que l'utilisateur est déjà en train de se faire demander par l'app s'il veut prendre une pause
    private final AtomicBoolean askingPause = new AtomicBoolean(false);

    // Boolean qui dicte si une pause est présentement prise par l'utilisateur
    private static final AtomicBoolean takingPause = new AtomicBoolean(false);

    // Si nous sommes présentement au milieu d'une période, cette fonction permet de détecter si une pause doit être prise
    @Override
    public void changedInPeriod(Period period, Number oldValue, Number timer) {
        // Vérifie qu'une pause peut légalement être prise
        PauseContainer pause = period.getPeriodType().getPauseContainer();
        Duration frequency = pause.getFrequency(), length = pause.getLength();
        boolean pauseNeeded = !frequency.isZero() && !length.isZero()
                && timer.longValue() != 0 && timer.longValue() % frequency.getSeconds() == 0;

        // Si l'utilisateur peut recevoir une notification de prise de pause
        if (pauseNeeded && !askingPause.get() && !takingPause.get()) {
            // Envoie de la notification
            askingPause.set(true);
            showPauseNotification(period.getPeriodType().getPauseContainer());
        }
    }

    private void showPauseNotification(PauseContainer pause) {
        Platform.runLater(() -> {
            boolean confirmation = AppManager.showConfirmation("C'est le temps de prendre une pause!", "Voulez-vous prendre une pause?");
            // La pause est confirmée par l'utilisateur
            if (confirmation) {
                // Pause prise
                pause.takePause();
                takingPause.set(true);
                // La pause sera arrêté après une période de temps équivalente à la longueur de la pause
                new Thread(() -> {
                    try {
                        Thread.sleep(pause.getLength().getSeconds() * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    takingPause.set(false);
                }).start();
            } else {
                // Pause ignorée
                takingPause.set(false);
            }
            // L'application peut à nouveau dedemander des pauses à l'utilisateur
            askingPause.set(false);
        });
    }

    // Partage à l'application le status de pause de l'utilisateur
    public static boolean isTakingPause() {
        return takingPause.get();
    }
}
