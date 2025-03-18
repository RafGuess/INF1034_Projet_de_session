package com.app.controllers;

import com.app.Program;
import com.app.controllers.factories.CalendarFactory;
import com.app.controllers.factories.PeriodFactory;
import com.app.utils.LocalDateUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class CalendarController {
    @FXML private Pane calendarPane;
    @FXML private Pane periodsPane;
    @FXML private Pane timesPane;
    @FXML private Pane dayPane;
    @FXML private Button previousWeekButton;
    @FXML private Button nextWeekButton;
    @FXML private VBox periodButtonsVBox;

    final private static int scaleDivisor = 10;
    private LocalDate currentFirstDayOfWeek;
    private final CalendarFactory calendarFactory = new CalendarFactory(
            Program.getWidthProperty().divide(scaleDivisor), Program.getHeightProperty().divide(scaleDivisor)
    );
    private final PeriodFactory periodFactory = new PeriodFactory(
            Program.getWidthProperty().divide(scaleDivisor)
    );

    public void drawScene() {
        currentFirstDayOfWeek = LocalDateUtils.getFirstDayOfWeek(LocalDate.now());

        calendarFactory.drawCalendarGrid(calendarPane);
        calendarFactory.drawTimes(timesPane);
        calendarFactory.resizePeriodButtons(periodButtonsVBox);
        calendarFactory.updateDayBar(dayPane, currentFirstDayOfWeek);

        periodFactory.updateShownPeriods(periodsPane, currentFirstDayOfWeek);

        previousWeekButton.prefWidthProperty().bind(timesPane.widthProperty());
        nextWeekButton.prefWidthProperty().bind(timesPane.widthProperty());

        periodsPane.prefWidthProperty().bind(calendarPane.widthProperty());
        periodsPane.prefHeightProperty().bind(calendarPane.heightProperty());
    }

    @FXML
    public void onNextWeekButtonClicked() {
        currentFirstDayOfWeek = currentFirstDayOfWeek.plusWeeks(1);
        updateCalendar();
    }

    @FXML
    public void onPreviousWeekButtonClicked() {
        currentFirstDayOfWeek = currentFirstDayOfWeek.minusWeeks(1);
        updateCalendar();
    }

    private void updateCalendar() {
        calendarFactory.updateDayBar(dayPane, currentFirstDayOfWeek);
        periodFactory.updateShownPeriods(periodsPane, currentFirstDayOfWeek);
    }

    @FXML
    public void print() {

    }
}