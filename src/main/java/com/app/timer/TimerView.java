package com.app.timer;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerView implements TimerListener {

    private Label timerLabel;

    public TimerView(Label timerLabel) {
        this.timerLabel = timerLabel;
    }

    // Met à jour l’affichage de l’heure du minuteur
    @Override
    public void changedTimer(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        StringBuilder labelText = new StringBuilder();
        labelText.append(formatter.format(LocalTime.ofSecondOfDay(newValue.intValue())));
        if (PauseNotificationHandler.isTakingPause() && newValue.intValue() != 0) {
            labelText.append(" - EN PAUSE");
        }

        // Met à jour le label avec l'heure formatée (sur le premier bouton dans la HBox)
        Platform.runLater(() -> timerLabel.setText(labelText.toString()));
    }
}
