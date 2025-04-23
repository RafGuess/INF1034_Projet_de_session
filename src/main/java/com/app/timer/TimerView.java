package com.app.timer;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerView implements TimerListener {

    HBox timerHBox;

    public TimerView(HBox timerHBox) {
        this.timerHBox = timerHBox;
    }

    // Met à jour l’affichage de l’heure du minuteur
    @Override
    public void changedTimer(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        StringBuilder labelText = new StringBuilder();
        labelText.append(formatter.format(LocalTime.ofSecondOfDay(newValue.intValue())));
        if (PauseNotificationHandler.isTakingPause()) {
            labelText.append("\nEN PAUSE");
        }

        // Met à jour le label avec l'heure formatée (sur le premier bouton dans la HBox)
        Platform.runLater(() -> ((Label) ((StackPane) timerHBox.getChildren().getFirst()).getChildren().getLast())
                .setText(labelText.toString()));
    }
}
