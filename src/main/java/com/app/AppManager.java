package com.app;

import com.app.controllers.Controller;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AppManager {
    private static double appWidth;
    private static double appHeight;
    private static Stage stage;

    private static final Map<String, Scene> scenes = new HashMap<>();
    private static final Map<String, Controller> controllers = new HashMap<>();

    public static void initialize(Stage stage, double appWidth, double appHeight) throws URISyntaxException, IOException {
        AppManager.stage = stage;
        AppManager.appWidth = appWidth;
        AppManager.appHeight = appHeight;
        buildAllScenes();
    }

    public static void showScene(String viewName) {
        stage.setScene(scenes.get(viewName));
        stage.show();
    }

    public static void rebuildThenShowScene(String viewName) {
        buildScene(viewName);
        showScene(viewName);
    }

    private static void buildScene(String viewName) {
        FXMLLoader fxmlLoader = new FXMLLoader(AppManager.class.getResource(viewName));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), appWidth, appHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Controller controller = fxmlLoader.getController();
        controller.drawScene();

        scenes.put(viewName, scene);
        controllers.put(viewName, controller);

    }

    private static void buildAllScenes() throws URISyntaxException, IOException {
        Files.walk(Paths.get(Objects.requireNonNull(AppManager.class.getClassLoader().getResource("com/app")).toURI()))
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".fxml"))
                .forEach((viewPath) -> buildScene(viewPath.getFileName().toString()));
    }

    public static ReadOnlyDoubleProperty getWidthProperty() {
        return stage.widthProperty();
    }

    public static ReadOnlyDoubleProperty getHeightProperty() {
        return stage.heightProperty();
    }
}
