package com.app.timer;

import com.app.models.Period;

import java.time.Duration;

public class ObjectivesUpdater implements InPeriodTimerListener {
    // Si nous sommes présentement au milieu d'une période, cette fonction permet de mettre à jour un objectif
    @Override
    public void changedInPeriod(Period period, Number oldValue, Number newValue) {
        if (newValue.longValue() - oldValue.longValue() >= 0) {
            Duration timeIncrement = Duration.ofSeconds(newValue.longValue() - oldValue.longValue());
            period.getPeriodType().updateCompletedTimeObjective(timeIncrement);
        }
    }
}
