package com.app;

import com.app.controllers.controllerInterfaces.Cleanable;
import com.app.controllers.controllerInterfaces.DataInitializable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.app.utils.ThemeManager;

import java.io.IOException;

public class AppManager {

    // Dimensions de la fenêtre principale
    private static double appWidth;
    private static double appHeight;

    // Fenêtre principale et fenêtre secondaire (popup)
    private static Stage primaryStage;
    private static Stage secondaryStage;

    // Vue actuellement affichée (ex : "calendar-view.fxml")
    private static String currentView = "";

    // Initialise l'application avec sa fenêtre principale et ses dimensions
    public static void setupApp(Stage stage, double appWidth, double appHeight) {
        AppManager.primaryStage = stage;
        AppManager.appWidth = appWidth;
        AppManager.appHeight = appHeight;
    }

    // Charge une scène principale à partir d'un fichier FXML
    public static <T> void showScene(String viewName, T dataToSend) {
        Scene scene = null;
        FXMLLoader fxmlLoader = buildFxmlLoader(viewName);

        try {
            scene = new Scene(fxmlLoader.load(), appWidth, appHeight);

            // Ajouter la feuille de style globale si pas déjà présente
            if (!scene.getStylesheets().contains("/com/app/styles/application.css")) {
                scene.getStylesheets().add(AppManager.class.getResource("/com/app/styles/application.css").toExternalForm());

            }
            // Mettre à jour la scène dans le ThemeManager
            ThemeManager.getInstance().setMainScene(scene);


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Affiche la scène dans la fenêtre principale
        primaryStage.setScene(scene);

        // Nettoie et initialise le contrôleur
        Object controller = fxmlLoader.getController();
        cleanStageController(controller);
        initControllerWithData(controller, dataToSend);

        primaryStage.show();

        // Enregistre la vue actuelle
        currentView = viewName;
    }

    // Affiche une popup modale à partir d’un FXML
    public static <T> void showPopup(String viewName, T dataToSend) {
        Scene secondaryScene = null;
        FXMLLoader fxmlLoader = buildFxmlLoader(viewName);

        try {
            secondaryScene = new Scene(fxmlLoader.load());

            // Ajouter la feuille de style globale si pas déjà présente
            if (!secondaryScene.getStylesheets().contains("/com/app/styles/application.css")) {
                secondaryScene.getStylesheets().add(AppManager.class.getResource("/com/app/styles/application.css").toExternalForm());
            }

            // Appliquer le thème actuel à la popup
            if (ThemeManager.getInstance().isDarkMode()) {
                secondaryScene.getRoot().getStyleClass().add("dark-theme");
                secondaryScene.getRoot().getStyleClass().remove("light-theme");
            } else {
                secondaryScene.getRoot().getStyleClass().add("light-theme");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crée et configure la fenêtre secondaire
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Popup");
        secondaryStage.setScene(secondaryScene);
        secondaryStage.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale

        // Nettoie et initialise le contrôleur de la popup
        Object controller = fxmlLoader.getController();
        cleanPopupController(controller, secondaryStage);
        initControllerWithData(fxmlLoader, dataToSend);

        // Affiche la popup et attend sa fermeture
        secondaryStage.showAndWait();
    }

    // Crée un loader FXML pour une vue donnée
    private static FXMLLoader buildFxmlLoader(String viewName) {
        return new FXMLLoader(AppManager.class.getResource(viewName));
    }

    // Appelle cleanup() si le contrôleur est de type Cleanable (pour une scène principale)
    private static void cleanStageController(Object controller) {
        primaryStage.sceneProperty().addListener(((observableValue, oldScene, newScene) -> {
            if (controller instanceof Cleanable) {
                ((Cleanable) controller).cleanup();
            }
        }));
    }

    // Appelle cleanup() si le contrôleur est de type Cleanable (pour une popup)
    private static void cleanPopupController(Object controller, Stage secondaryStage) {
        secondaryStage.onCloseRequestProperty().addListener(((observableValue, oldScene, newScene) -> {
            if (controller instanceof Cleanable) {
                ((Cleanable) controller).cleanup();
            }
        }));
    }

    // Initialise un contrôleur avec des données si celui-ci implémente DataInitializable<T>
    private static <T> void initControllerWithData(Object controller, T info) {
        if (info != null && controller instanceof DataInitializable) {
            // Suppress warning (cast non vérifié)
            ((DataInitializable<T>) controller).initializeWithData(info);
        }
    }

    // Accès à la propriété largeur de la fenêtre principale (binding possible)
    public static ReadOnlyDoubleProperty getWidthProperty() {
        return primaryStage.widthProperty();
    }

    // Accès à la propriété hauteur de la fenêtre principale
    public static ReadOnlyDoubleProperty getHeightProperty() {
        return primaryStage.heightProperty();
    }

    // Retourne le nom du fichier FXML de la vue actuellement affichée
    public static String getCurrentView() {
        return currentView;
    }
}
