package com.app.timer;

import com.app.models.Period;

public interface InPeriodTimerListener {
    void changedInPeriod(Period period, Number oldValue, Number timer);
}
