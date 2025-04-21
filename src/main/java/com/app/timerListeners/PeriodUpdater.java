package com.app.timerListeners;

import com.app.models.Database;
import com.app.models.Period;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.LocalTime;

// Classe abstraite qui roule un bout de code définit dans run() si une période est présentement active, et ce,
// à chaque seconde écoulée par le Timer.
public abstract class PeriodUpdater implements TickUpdater {

    // Méthode de l'interface TickUpdater qui correspond à un listener du Timer
    public void update(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        // Pour chaque période de l'utilisateur connecté
        for (Period period : Database.getPeriodsOfUser(Database.getConnectedUser())) {
            // Si nous sommes présentement dans une période active
            if (LocalDate.now().isEqual(period.getDate()) &&
                    LocalTime.now().isAfter(period.getStartTime()) &&
                    LocalTime.now().isBefore(period.getEndTime())
            ) {
                // Exécuter ce code polymorphique
                run(period, oldValue, newValue);
                // Return car seulement une période peut être active à la fois
                return;
            }
        }
    }

    // Méthode redéfinit par les enfants, exécuter quand une période est présentement active.
    protected abstract void run(Period period, Number oldValue, Number newValue);
}
