package com.app.controllers.factories;

import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PeriodFactory {
    final private ObservableDoubleValue calendarCellWidth;

    public PeriodFactory(ObservableDoubleValue calendarCellWidth) {
        this.calendarCellWidth = calendarCellWidth;
    }

    public void updateShownPeriods(Pane periodsPane, LocalDate firstDayOfWeek, ObservableList<Period> periods) { //todo: will need the periods list as a param
        periodsPane.getChildren().clear();
        for (Period period : periods) {
            if (LocalDateUtils.getFirstDayOfWeek(period.getDate()).equals(firstDayOfWeek)) {
                Button periodButton = makePeriodButton(periodsPane.heightProperty(), period);

                long i = ChronoUnit.DAYS.between(firstDayOfWeek, period.getDate());
                periodButton.layoutXProperty().bind(Bindings.multiply(calendarCellWidth, (double)i+0.05));
                periodButton.layoutYProperty().bind(Bindings.multiply(
                                periodsPane.heightProperty(), (double)period.getStartTime().toSecondOfDay()/86400)
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

        Duration periodLength = Duration.between(period.getStartTime(), period.getEndTime());
        periodButton.prefWidthProperty().bind(Bindings.multiply(calendarCellWidth, 0.9));
        periodButton.prefHeightProperty().bind(
                Bindings.multiply(periodsPaneHeightProperty, (double) periodLength.getSeconds() / 86400)
        );
        return periodButton;
    }

}
