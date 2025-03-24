package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.factories.CalendarFactory;
import com.app.controllers.factories.PeriodFactory;
import com.app.controllers.viewModels.PeriodView;
import com.app.models.DataModel;
import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarController implements Cleanable {
    @FXML private VBox root;
    @FXML private Pane calendarPane;
    @FXML private Pane periodsPane;
    @FXML private Pane currentTimePane;
    @FXML private Line currentTimeLine;
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
    private AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            double fractionOfDay = (double) LocalTime.now().toSecondOfDay() / 86400;
            currentTimeLine.startYProperty().bind(Bindings.multiply(currentTimePane.heightProperty(), fractionOfDay));
            currentTimeLine.endYProperty().bind(Bindings.multiply(currentTimePane.heightProperty(), fractionOfDay));
            currentTimeLine.endXProperty().bind(currentTimePane.widthProperty());
        }
    };

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

        currentTimePane.prefWidthProperty().bind(calendarPane.widthProperty());
        currentTimePane.prefHeightProperty().bind(calendarPane.heightProperty());

        periodListChangeListener = change -> updateCalendar();
        DataModel.addListenerToPeriodsOfUser(DataModel.getConnectedUser(), periodListChangeListener);

        timer.start();
    }

    @Override
    public void cleanup() {
        DataModel.removeListenerFromPeriodsOfUser(DataModel.getConnectedUser(), periodListChangeListener);
        timer.stop();
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

    public void onPeriodCanceled(ActionEvent actionEvent) {
        DataModel.removePeriod(((PeriodView)actionEvent.getSource()).getPeriod());
    }

    public void onPeriodAccessed(ActionEvent actionEvent) {
        // todo: should be a pop up.
        AppManager.showSceneAndInjectInfo(
                "show-period-view.fxml", ((PeriodView)actionEvent.getSource()).getPeriod()
        );
    }

    public void onPeriodMoved(ActionEvent actionEvent) {

    }

    @FXML
    private void updateCalendar() {
        EventHandler<ActionEvent> actionEvent = this::onPeriodAccessed;
        if (cancelPeriodButton.isSelected() && movePeriodButton.isSelected()) {
            cancelPeriodButton.setSelected(false);
            movePeriodButton.setSelected(false);
        } else if (movePeriodButton.isSelected()) {
            // todo
        } else if (cancelPeriodButton.isSelected()) {
            actionEvent = this::onPeriodCanceled;
        }

        periodFactory.updateShownPeriods(
                periodsPane, currentFirstDayOfWeek,
                DataModel.getPeriodsOfUser(DataModel.getConnectedUser()),
                actionEvent
        );
        calendarFactory.updateDayBar(dayPane, currentFirstDayOfWeek);
    }

    @FXML
    public void print() {

    }
}