package com.app.controllers.customNodes;

import com.app.models.Period;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class PeriodNode extends Button {
    private final Period period;
    private final Label periodTimeLabel;

    public double offsetY = 0;
    public double offsetX;
    public boolean beingMoved = false;

    public PeriodNode(Period period) {
        super();
        this.period = period;
        this.periodTimeLabel = new Label();

        periodTimeLabel.getStyleClass().add("period-time-label");
        periodTimeLabel.layoutXProperty().bind(this.layoutXProperty()
                .subtract(periodTimeLabel.widthProperty())
                .add(this.widthProperty()).add(5));
        periodTimeLabel.layoutYProperty().bind(this.layoutYProperty().subtract(10));

    }

    public Period getPeriod() {
        return period;
    }

    public Label getAssociatedLabel() {
        return periodTimeLabel;
    }
}
