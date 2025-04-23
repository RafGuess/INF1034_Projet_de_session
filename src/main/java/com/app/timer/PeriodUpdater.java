package com.app.timer;

import com.app.models.Database;
import com.app.models.Period;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Classe abstraite qui s'occupe de déclencher des listeners du Timer si une période est présentement active, et ce,
// à chaque seconde écoulée par le Timer.
public class PeriodUpdater implements TimerListener {

    // Liste des listeners
    List<InPeriodTimerListener> listeners = new ArrayList<>();

    public PeriodUpdater(InPeriodTimerListener ... listeners ) {
        for (InPeriodTimerListener listener : listeners) {
            addInPeriodListener(listener);
        }
    }

    // Méthode de l'interface TickUpdater qui correspond à un listener du Timer
    @Override
    public void changedTimer(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        // Pour chaque période de l'utilisateur connecté
        for (Period period : Database.getPeriodsOfUser(Database.getConnectedUser())) {
            // Si nous sommes présentement dans une période active
            if (LocalDate.now().isEqual(period.getDate()) &&
                    LocalTime.now().isAfter(period.getStartTime()) &&
                    LocalTime.now().isBefore(period.getEndTime())
            ) {
                // Appel des listeners
                for (InPeriodTimerListener listener : listeners) {
                    listener.changedInPeriod(period, oldValue, newValue);
                }
                return;
            }
        }
    }

    public void addInPeriodListener(InPeriodTimerListener timerListener) {
        listeners.add(timerListener);
    }

    public void removeInPeriodListener(InPeriodTimerListener timerListener) {
        listeners.remove(timerListener);
    }
}
