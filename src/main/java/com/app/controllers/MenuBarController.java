package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.factories.MenuBarFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuBarController {
    @FXML private Button calendarButton;
    @FXML private Button tasksButton;
    @FXML private Button statisticsButton;
    @FXML private Button parametersButton;

    private final MenuBarFactory menuBarFactory = new MenuBarFactory(
            AppManager.getWidthProperty(), AppManager.getHeightProperty()
    );

    public void initialize() {
        menuBarFactory.resizeMenuButtons(calendarButton, tasksButton, statisticsButton, parametersButton);
    }

    @FXML
    public void onCalendarButtonClicked() {
        AppManager.showScene("calendar-view.fxml", null);
    }

    public void onTasksButtonClicked() {
        // todo
    }

    public void onStatisticsButtonClicked() {
        // todo
    }

    public void onParametersButtonClicked() {
        // todo
    }
}
