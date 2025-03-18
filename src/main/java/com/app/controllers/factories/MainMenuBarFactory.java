package com.app.controllers.factories;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class MainMenuBarFactory {
    final private ObservableDoubleValue appWidth;
    final private ObservableDoubleValue appHeight;

    public MainMenuBarFactory(ObservableDoubleValue appWidth, ObservableDoubleValue appHeight) {
        this.appWidth = appWidth;
        this.appHeight = appHeight;
    }

    public void drawMenuBar(HBox menuHBox) {
        menuHBox.getChildren().clear();

        Button calendarButton = new Button("Calendrier");
        Button tasksButton = new Button("Tâches");
        Button statisticsButton = new Button("Statistiques");
        Button parametersButton = new Button("Paramètres");

        calendarButton.prefWidthProperty().bind(Bindings.multiply(appWidth,0.3));
        tasksButton.prefWidthProperty().bind(Bindings.multiply(appWidth,0.3));
        statisticsButton.prefWidthProperty().bind(Bindings.multiply(appWidth, 0.3));
        parametersButton.prefWidthProperty().bind(Bindings.multiply(appWidth, 0.1));

        //calendarButton.setOnAction();
    }
}
