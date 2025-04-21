package com.app.timerListeners;

import com.app.models.Period;

import java.time.Duration;

public class ObjectivesUpdater extends PeriodUpdater {

    // Si nous sommes présentement au milieu d'une période, cette fonction permet de mettre à jour un objectif
    protected void run(Period period, Number oldValue, Number newValue) {
        Duration timeIncrement = Duration.ofSeconds(newValue.longValue() - oldValue.longValue());
        period.getPeriodType().updateCompletedTimeObjective(timeIncrement);
    }
}
