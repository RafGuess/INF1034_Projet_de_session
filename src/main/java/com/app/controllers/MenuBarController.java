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
        // Ajustement de taille
        menuBarFactory.resizeMenuButtons(calendarButton, tasksButton, statisticsButton, parametersButton);

        // Supprimer le focus automatique (évite l'effet bleu autour des boutons)
        calendarButton.setFocusTraversable(false);
        tasksButton.setFocusTraversable(false);
        statisticsButton.setFocusTraversable(false);
        parametersButton.setFocusTraversable(false);
    }

    @FXML
    public void onCalendarButtonClicked() {
        if (!AppManager.getCurrentView().equals("calendar-view.fxml")) {
            AppManager.showScene("calendar-view.fxml", null);
        }
    }

    @FXML
    public void onTasksButtonClicked() {
        if (!AppManager.getCurrentView().equals("tasks-view.fxml")) {
            AppManager.showScene("tasks-view.fxml", null);
        }
    }

    @FXML
    public void onStatisticsButtonClicked() {
        if (!AppManager.getCurrentView().equals("statistics-view.fxml")) {
            AppManager.showScene("statistics-view.fxml", null);
        }
    }

    @FXML
    public void onParametersButtonClicked() {
        if (!AppManager.getCurrentView().equals("parameters-view.fxml")) {
            AppManager.showScene("parameters-view.fxml", null);
        }
    }
}
