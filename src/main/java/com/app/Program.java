package com.app;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

public class Program extends Application {
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        stage.setTitle("ProductivityApp");

        AppManager.initialize(stage, 1100, 800);
        AppManager.showScene("calendar-view.fxml");

    }

    public static void main(String[] args) {
        launch();
    }

}