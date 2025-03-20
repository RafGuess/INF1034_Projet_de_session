package com.app.controllers.factories;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.control.Button;

public class MenuBarFactory {
    final private ObservableDoubleValue appWidth;
    final private ObservableDoubleValue appHeight;

    public MenuBarFactory(ObservableDoubleValue appWidth, ObservableDoubleValue appHeight) {
        this.appWidth = appWidth;
        this.appHeight = appHeight;
    }

    public void resizeMenuButtons(Button calendarButton, Button tasksButton, Button statisticsButton, Button parametersButton) {
        calendarButton.prefWidthProperty().bind(Bindings.multiply(appWidth,0.3));
        tasksButton.prefWidthProperty().bind(Bindings.multiply(appWidth,0.3));
        statisticsButton.prefWidthProperty().bind(Bindings.multiply(appWidth, 0.3));
        parametersButton.prefWidthProperty().bind(Bindings.multiply(appWidth, 0.1));
    }
}
