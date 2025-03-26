package com.app.controllers.factories;

import com.app.controllers.viewModels.PeriodView;
import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PeriodFactory {
    final private ObservableDoubleValue calendarCellWidth;

    public PeriodFactory(ObservableDoubleValue calendarCellWidth) {
        this.calendarCellWidth = calendarCellWidth;
    }

    public void updateShownPeriods(Pane periodsPane, LocalDate firstDayOfWeek,
                                   ObservableList<Period> periods, EventHandler<ActionEvent> buttonsActionEvent
    ) {
        periodsPane.getChildren().clear();
        for (Period period : periods) {
            if (LocalDateUtils.getFirstDayOfWeek(period.getDate()).equals(firstDayOfWeek)) {
                PeriodView periodButton = makePeriodButton(periodsPane.heightProperty(), period, buttonsActionEvent);

                long i = ChronoUnit.DAYS.between(firstDayOfWeek, period.getDate());
                periodButton.layoutXProperty().bind(Bindings.multiply(calendarCellWidth, (double)i+0.05));
                periodButton.layoutYProperty().bind(Bindings.multiply(
                                periodsPane.heightProperty(), (double)period.getStartTime().toSecondOfDay()/86400)
                );

                periodsPane.getChildren().add(periodButton);
            }
        }
    }

    private PeriodView makePeriodButton(ObservableDoubleValue periodsPaneHeightProperty,
                                        Period period, EventHandler<ActionEvent> actionEvent
    ) {
        PeriodView periodButton = new PeriodView(period);
        periodButton.setText(period.getPeriodType().getTitle());
        periodButton.setStyle(
                "-fx-background-color: " + period.getPeriodType().getRGBColor() + ";"
        );

        periodButton.prefWidthProperty().bind(Bindings.multiply(calendarCellWidth, 0.9));
        periodButton.prefHeightProperty().bind(
                Bindings.multiply(periodsPaneHeightProperty, (double) period.getDuration().getSeconds() / 86400)
        );

        periodButton.setOnAction(actionEvent);

        return periodButton;
    }

}
