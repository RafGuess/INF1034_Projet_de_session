package com.app;

import com.app.controllers.Cleanable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppManager {
    private static double appWidth;
    private static double appHeight;
    private static Stage stage;
    private static Scene scene;
    private static Object currentController;

    public static void setupApp(Stage stage, double appWidth, double appHeight) {
        AppManager.stage = stage;
        AppManager.appWidth = appWidth;
        AppManager.appHeight = appHeight;
    }

    public static void showScene(String viewName) {
        if (currentController instanceof Cleanable) {
            ((Cleanable)currentController).cleanup();
        }

        stage.setScene(buildScene(viewName));
        stage.show();
    }

    private static Scene buildScene(String viewName) {
        FXMLLoader fxmlLoader = new FXMLLoader(AppManager.class.getResource(viewName));
        try {
            scene = new Scene(fxmlLoader.load(), appWidth, appHeight);
            currentController = fxmlLoader.getController();
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
