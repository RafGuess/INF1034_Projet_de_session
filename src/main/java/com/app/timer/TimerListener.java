package com.app.timer;

import javafx.beans.value.ObservableValue;

public interface TimerListener {
    void changedTimer(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue);
}
