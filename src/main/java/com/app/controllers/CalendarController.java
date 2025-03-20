package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.factories.CalendarFactory;
import com.app.controllers.factories.PeriodFactory;
import com.app.models.DataModel;
import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class CalendarController implements Cleanable {
    @FXML private VBox root;
    @FXML private Pane calendarPane;
    @FXML private Pane periodsPane;
    @FXML private Pane timesPane;
    @FXML private Pane dayPane;
    @FXML private Button previousWeekButton;
    @FXML private Button nextWeekButton;
    @FXML private VBox periodButtonsVBox;
    @FXML private Button addPeriodButton;
    @FXML private ToggleButton movePeriodButton;
    @FXML private ToggleButton cancelPeriodButton;

    final private static int scaleDivisor = 10;
    private LocalDate currentFirstDayOfWeek;
    private final CalendarFactory calendarFactory = new CalendarFactory(
            AppManager.getWidthProperty().divide(scaleDivisor), AppManager.getHeightProperty().divide(scaleDivisor)
    );
    private final PeriodFactory periodFactory = new PeriodFactory(
            AppManager.getWidthProperty().divide(scaleDivisor)
    );
    private ListChangeListener<Period> periodListChangeListener;

    public void initialize() {
        currentFirstDayOfWeek = LocalDateUtils.getFirstDayOfWeek(LocalDate.now());

        calendarFactory.drawCalendarGrid(calendarPane);
        calendarFactory.drawTimes(timesPane);
        calendarFactory.resizePeriodButtons(periodButtonsVBox);

        updateCalendar();

        previousWeekButton.prefWidthProperty().bind(timesPane.widthProperty());
        nextWeekButton.prefWidthProperty().bind(timesPane.widthProperty());

        periodsPane.prefWidthProperty().bind(calendarPane.widthProperty());
        periodsPane.prefHeightProperty().bind(calendarPane.heightProperty());

        periodListChangeListener = change -> updateCalendar();
        DataModel.addListenerToPeriodsOfUser(DataModel.getConnectedUser(), periodListChangeListener);
    }

    @Override
    public void cleanup() {
        DataModel.removeListenerFromPeriodsOfUser(DataModel.getConnectedUser(), periodListChangeListener);
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
        if (cancelPeriodButton.isSelected()) {
            periodsPane.getChildren().forEach(button ->
                    ((Button)button).setOnAction(actionEvent -> periodsPane.getChildren().remove(button))
            );
            System.out.println("button can be deleted");
        } else {
            periodsPane.getChildren().forEach(button ->
                ((Button)button).setOnAction(actionEvent -> onPeriodClicked())
            );
            System.out.println("button can no longer be deleted");
        }
    }

    public void onPeriodClicked() {
        // todo
    }

    private void updateCalendar() {
        calendarFactory.updateDayBar(dayPane, currentFirstDayOfWeek);
        periodFactory.updateShownPeriods(
                periodsPane, currentFirstDayOfWeek, DataModel.getPeriodsOfUser(DataModel.getConnectedUser())
        );
    }

    @FXML
    public void print() {

    }
}