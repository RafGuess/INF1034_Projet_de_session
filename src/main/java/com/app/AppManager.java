package com.app;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class AppManager {
    private static double appWidth;
    private static double appHeight;
    private static Stage stage;

    private static final Map<String, Scene> scenes = new HashMap<>();

    public static void setupApp(Stage stage, double appWidth, double appHeight) throws URISyntaxException, IOException {
        AppManager.stage = stage;
        AppManager.appWidth = appWidth;
        AppManager.appHeight = appHeight;
    }

    public static void showScene(String viewName) {
        stage.setScene(buildScene(viewName));
        stage.show();
    }

    private static Scene buildScene(String viewName) {
        FXMLLoader fxmlLoader = new FXMLLoader(AppManager.class.getResource(viewName));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), appWidth, appHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scene;
    }

    public static ReadOnlyDoubleProperty getWidthProperty() {
        return stage.widthProperty();
    }

    public static ReadOnlyDoubleProperty getHeightProperty() {
        return stage.heightProperty();
    }
}
