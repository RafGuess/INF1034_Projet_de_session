package com.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class CalendarController {
    @FXML private Pane calendarPane;
    @FXML private Pane timesPane;
    @FXML private Pane dayPane;
    @FXML private Button previousWeekButton;
    @FXML private Button nextWeekButton;
    @FXML private VBox periodButtonsVBox;

    public void drawScene() {
        CalendarFactory calendarFactory = new CalendarFactory(
                calendarPane, timesPane, dayPane, previousWeekButton, nextWeekButton, periodButtonsVBox
        );
        calendarFactory.drawCalendarGrid();
        calendarFactory.drawTimes();
        calendarFactory.resizeButtons();
        calendarFactory.drawDayBar();
    }


    @FXML
    public void print() {
    }
}