package com.app;

import com.app.models.Timer;
import javafx.application.Application;
import javafx.stage.Stage;

public class Program extends Application {
    @Override
    public void start(Stage stage)  {
        stage.setTitle("ProductivityApp");

        AppManager.setupApp(stage, 1100, 800);
        AppManager.showScene("calendar-view.fxml", null);

    }

    public static void main(String[] args) {
        launch();
        Timer.stopTimer();
    }

}