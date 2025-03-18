package com.app.controllers.factories;

import com.app.models.PeriodType;
import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PeriodFactory {
    List<Period> periods = new ArrayList<>(); // todo: will have to be moved elsewhere

    final private ObservableDoubleValue calendarCellWidth;

    public PeriodFactory(ObservableDoubleValue calendarCellWidth) {
        this.calendarCellWidth = calendarCellWidth;
        PeriodType testPeriodType = new PeriodType("Activité", Color.GREEN);
        Period testPeriod = new Period(
                LocalDate.now(),
                LocalTime.of(5,0,0),
                LocalTime.of(9,0,0),
                testPeriodType,
                "Une nice periode"
        );
        periods.add(testPeriod);
    }

    public void updateShownPeriods(Pane periodsPane, LocalDate firstDayOfWeek) {
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
        System.out.println(period.getPeriodType().getRGBColor());
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
