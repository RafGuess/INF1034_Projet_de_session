package com.app.controllers;

import com.app.utils.LocalDateUtils;
import com.app.models.Period;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PeriodFactory {
    List<Period> periods = new ArrayList<>(); // todo: will have to be moved elsewhere

    final private ObservableDoubleValue calendarCellWidth;

    public PeriodFactory(ObservableDoubleValue calendarCellWidth) {
        this.calendarCellWidth = calendarCellWidth;
    }

    public void updateShownPeriods(Pane periodsPane, LocalDate firstDayOfWeek) {
        periodsPane.getChildren().clear();
        for (Period period : periods) {
            if (LocalDateUtils.getFirstDayOfWeek(period.getDate()).equals(firstDayOfWeek)) {
                Button periodButton = makePeriodButton(periodsPane.prefHeightProperty(), period);

                long i = ChronoUnit.DAYS.between(firstDayOfWeek, period.getDate());
                periodButton.layoutXProperty().bind(Bindings.multiply(calendarCellWidth, i));
                periodButton.layoutYProperty().bind(Bindings.multiply(
                                periodsPane.heightProperty(), (double)period.getStartTime().getSecond()/86400)
                );

                periodsPane.getChildren().add(periodButton);
            }
        }
    }

    private Button makePeriodButton(ObservableDoubleValue periodsPaneHeightProperty, Period period) {
        Button periodButton = new Button();
        periodButton.setText(period.getPeriodType().getTitle());
        periodButton.setStyle(
                "-fx-background-color: " + period.getPeriodType().getRGBColor() + ";"
        );

        Duration periodLength = Duration.between(period.getEndTime(), period.getStartTime());
        periodButton.prefWidthProperty().bind(Bindings.multiply(calendarCellWidth, 0.9));
        periodButton.prefHeightProperty().bind(
                Bindings.multiply(periodsPaneHeightProperty, (double) periodLength.getSeconds() / 86400)
        );
        return periodButton;
    }

}
