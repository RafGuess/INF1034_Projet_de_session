package com.app.controllers.viewModels;

import com.app.models.Period;
import javafx.scene.control.Button;

public class PeriodView extends Button {
    private final Period period;

    public PeriodView(Period period) {
        super();
        this.period = period;
    }

    public Period getPeriod() {
        return period;
    }
}
