package com.app.controllers.customNodes;

import com.app.models.Period;
import javafx.scene.control.Button;

public class PeriodNode extends Button {
    private final Period period;

    public double offsetY = 0;
    public double offsetX;
    public boolean beingMoved = false;

    public PeriodNode(Period period) {
        super();
        this.period = period;
    }

    public Period getPeriod() {
        return period;
    }
}
