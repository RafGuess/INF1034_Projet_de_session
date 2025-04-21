package com.app.timerListeners;

import javafx.beans.value.ObservableValue;

public interface TickUpdater {
    void update(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue);
}
