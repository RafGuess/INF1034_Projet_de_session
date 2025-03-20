package com.app.controllers;

import com.app.AppManager;
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
            AppManager.getWidthProperty().divide(scaleDivisor), AppManager.getHeightProperty().divide(scaleDivisor)
    );
    private final PeriodFactory periodFactory = new PeriodFactory(
            AppManager.getWidthProperty().divide(scaleDivisor)
    );

    public void initialize() {
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

    @FXML
    public void onCreatePeriodButtonClicked() {
        AppManager.showScene("add-period-view.fxml");
    }

    @FXML
    public void onMovePeriodButtonClicked() {
        // todo
    }

    @FXML
    public void onCancelPeriodButtonClicked() {
        // todo
    }

    private void updateCalendar() {
        calendarFactory.updateDayBar(dayPane, currentFirstDayOfWeek);
        periodFactory.updateShownPeriods(periodsPane, currentFirstDayOfWeek);
    }

    @FXML
    public void print() {

    }
}