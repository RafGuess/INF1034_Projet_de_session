package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.factories.CalendarFactory;
import com.app.controllers.factories.PeriodFactory;
import com.app.controllers.viewModels.PeriodView;
import com.app.models.Database;
import com.app.models.Period;
import com.app.models.Timer;
import com.app.utils.LocalDateUtils;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CalendarController implements Cleanable {

    // Calendar rectangles, periods and time line
    @FXML private Pane calendarPane;
    @FXML private Pane periodsPane;
    @FXML private Pane currentTimePane;
    @FXML private Line currentTimeLine;

    // Time labels
    @FXML private Pane timesPane;

    // Day bar above the calendar
    @FXML private Pane dayPane;
    @FXML private Button previousWeekButton;
    @FXML private Button nextWeekButton;

    // Buttons for period mofifications
    @FXML private VBox periodButtonsVBox;
    @FXML private Button addPeriodButton;
    @FXML private ToggleButton movePeriodButton;
    @FXML private ToggleButton cancelPeriodButton;

    // Timer buttons
    @FXML private HBox timerHBox;

    // First day of the week to track which week to draw on screen
    private LocalDate currentFirstDayOfWeek;

    // Scale factor for UI elements. Would not touch.
    final private static int scaleDivisor = 9;

    // Factories instantiation
    private final CalendarFactory calendarFactory = new CalendarFactory(
            AppManager.getWidthProperty().divide(scaleDivisor), AppManager.getHeightProperty().divide(scaleDivisor)
    );

    private final PeriodFactory periodFactory = new PeriodFactory(
            AppManager.getWidthProperty().divide(scaleDivisor)
    );

    // Listener for updating calender when periods are added
    private ListChangeListener<Period> periodListChangeListener;

    // Thread that updates the time line and timer
    private final AnimationTimer continuousUpdateThread = new AnimationTimer() {
        @Override
        public void handle(long l) {
            // Time line update
            double fractionOfDay = (double) LocalTime.now().toSecondOfDay() / 86400;
            currentTimeLine.startYProperty().bind(Bindings.multiply(currentTimePane.heightProperty(), fractionOfDay));
            currentTimeLine.endYProperty().bind(Bindings.multiply(currentTimePane.heightProperty(), fractionOfDay));
            currentTimeLine.endXProperty().bind(currentTimePane.widthProperty());
        }
    };

    // Controller initialization
    public void initialize() {
        currentFirstDayOfWeek = LocalDateUtils.getFirstDayOfWeek(LocalDate.now()); // could be done above...

        // Drawing UI elements
        calendarFactory.drawCalendarGrid(calendarPane);
        calendarFactory.drawTimes(timesPane);
        calendarFactory.resizePeriodButtons(periodButtonsVBox);
        calendarFactory.makeTimerButtons(timerHBox, this::onStartTimer, this::onResetTimer, this::onStopTimer);

        // Updating calendar with periods
        updateCalendar();

        // Setting UI elements sizes
        previousWeekButton.prefWidthProperty().bind(timesPane.widthProperty());
        nextWeekButton.prefWidthProperty().bind(timesPane.widthProperty());

        periodsPane.prefWidthProperty().bind(calendarPane.widthProperty());
        periodsPane.prefHeightProperty().bind(calendarPane.heightProperty());
        currentTimePane.prefWidthProperty().bind(calendarPane.widthProperty());
        currentTimePane.prefHeightProperty().bind(calendarPane.heightProperty());

        // Making this pane transparent so that periods can be clicked (this pane is above periodsPane)
        currentTimeLine.setMouseTransparent(true);

        // Setting up listener for updating the calendar upon adding periods
        periodListChangeListener = change -> updateCalendar();
        Database.addListenerToPeriodsOfUser(Database.getConnectedUser(), periodListChangeListener);

        // Setting up listener for updating the timer UI
        Timer.addListener(this::updateTimer);
        updateTimer(null, 0,0);

        // Starting time line
        continuousUpdateThread.start();
    }

    // Cleans up the controller before collection by the garbage collector
    @Override
    public void cleanup() {
        Database.removeListenerFromPeriodsOfUser(Database.getConnectedUser(), periodListChangeListener);
        continuousUpdateThread.stop();
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
        Database.removePeriod(((PeriodView)actionEvent.getSource()).getPeriod());
    }

    public void onPeriodAccessed(ActionEvent actionEvent) {
        // todo: should be a pop up.
        AppManager.showSceneAndInjectInfo(
                "show-period-view.fxml", ((PeriodView)actionEvent.getSource()).getPeriod()
        );
    }

    public void onPeriodMoved(ActionEvent actionEvent) {
        //todo, maybe unecessary
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
                Database.getPeriodsOfUser(Database.getConnectedUser()),
                actionEvent
        );
        calendarFactory.updateDayBar(dayPane, currentFirstDayOfWeek);
    }

    private void onStartTimer(MouseEvent mouseEvent) {
        Timer.startTimer();
    }

    private void onResetTimer(MouseEvent mouseEvent) {
        Timer.resetTimer();
    }

    private void onStopTimer(MouseEvent mouseEvent) {
        Timer.stopTimer();
    }

    private void updateTimer(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Platform.runLater(() -> ((Label)((StackPane)timerHBox.getChildren().getFirst()).getChildren().getLast())
                .setText(formatter.format(LocalTime.ofSecondOfDay(newValue.intValue()))));
    }

    @FXML
    public void print() {

    }
}