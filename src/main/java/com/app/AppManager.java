package com.app;

import com.app.controllers.controllerInterfaces.Cleanable;
import com.app.controllers.controllerInterfaces.DataInitializable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AppManager {
    private static double appWidth;
    private static double appHeight;

    private static Stage primaryStage;
    private static Stage secondaryStage;

    // ✅ Ajout : vue actuellement affichée
    private static String currentView = "";

    public static void setupApp(Stage stage, double appWidth, double appHeight) {
        AppManager.primaryStage = stage;
        AppManager.appWidth = appWidth;
        AppManager.appHeight = appHeight;
    }

    public static <T> void showScene(String viewName, T dataToSend) {
        Scene scene = null;
        FXMLLoader fxmlLoader = buildFxmlLoader(viewName);

        try {
            scene = new Scene(fxmlLoader.load(), appWidth, appHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setScene(scene);

        Object controller = fxmlLoader.getController();
        cleanStageController(controller);
        initControllerWithData(controller, dataToSend);

        primaryStage.show();

        // ✅ Enregistre le nom de la vue actuellement affichée
        currentView = viewName;
    }

    public static <T> void showPopup(String viewName, T dataToSend) {
        Scene secondaryScene = null;
        FXMLLoader fxmlLoader = buildFxmlLoader(viewName);

        try {
            secondaryScene = new Scene(fxmlLoader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage secondaryStage = new Stage();

        secondaryStage.setTitle("Popup");
        secondaryStage.setScene(secondaryScene);
        secondaryStage.initModality(Modality.APPLICATION_MODAL);

        Object controller = fxmlLoader.getController();
        cleanPopupController(controller, secondaryStage);
        initControllerWithData(fxmlLoader, dataToSend);

        secondaryStage.showAndWait();
    }

    private static FXMLLoader buildFxmlLoader(String viewName) {
        return new FXMLLoader(AppManager.class.getResource(viewName));
    }

    private static void cleanStageController(Object controller) {
        primaryStage.sceneProperty().addListener(((observableValue, oldScene, newScene) -> {
            if (controller instanceof Cleanable) {
                ((Cleanable) controller).cleanup();
            }
        }));
    }

    private static void cleanPopupController(Object controller, Stage secondaryStage) {
        secondaryStage.onCloseRequestProperty().addListener(((observableValue, oldScene, newScene) -> {
            if (controller instanceof Cleanable) {
                ((Cleanable) controller).cleanup();
            }
        }));
    }

    private static <T> void initControllerWithData(Object controller, T info) {
        if (info != null && controller instanceof DataInitializable) {
            //unsafe
            ((DataInitializable<T>) controller).initializeWithData(info);
        }
    }

    public static ReadOnlyDoubleProperty getWidthProperty() {
        return primaryStage.widthProperty();
    }

    public static ReadOnlyDoubleProperty getHeightProperty() {
        return primaryStage.heightProperty();
    }

    // ✅ Getter pour la vue actuelle
    public static String getCurrentView() {
        return currentView;
    }
}
