package com.app.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {
    private static final SimpleIntegerProperty seconds = new SimpleIntegerProperty(-1);

    private static ScheduledExecutorService chronometerExecutor;

    public static void startTimer() {
        if (chronometerExecutor != null && !chronometerExecutor.isShutdown()) {
            return;
        }
        chronometerExecutor = Executors.newSingleThreadScheduledExecutor();
        chronometerExecutor.scheduleAtFixedRate(Timer::incrementTime, 0, 1, TimeUnit.SECONDS);
    }

    private static void incrementTime() {
        seconds.set(seconds.get() + 1);
    }

    public static void addListener(ChangeListener<Number> listener) {
        seconds.addListener(listener);
    }

    public static void removeListener(ChangeListener<Number> listener) {
        seconds.removeListener(listener);
    }

    public static void stopTimer() {
        if (chronometerExecutor != null) {
            chronometerExecutor.shutdown();
        }
    }

    public static void resetTimer() {
        stopTimer();
        seconds.set(-1);
    }
}
