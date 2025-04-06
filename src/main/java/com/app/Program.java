package com.app;

import com.app.models.Timer;
import javafx.application.Application;
import javafx.stage.Stage;
import com.app.utils.ThemeManager;
import javafx.scene.Scene;
public class Program extends Application {

    // Point d'entrée JavaFX (appelé automatiquement après launch())
    @Override
    public void start(Stage stage) {
        // Définit le titre de la fenêtre principale
        stage.setTitle("ProductivityApp");

        // Initialise l'application avec la fenêtre principale et ses dimensions
        AppManager.setupApp(stage, 1100, 800);

        // Récupère la scène principale pour configurer le ThemeManager
        Scene mainScene = stage.getScene();
        if (mainScene != null) {
            // Ajoute la feuille de style globale pour les thèmes
            mainScene.getStylesheets().add(getClass().getResource("/com/app/styles/application.css").toExternalForm());

            // Initialise le ThemeManager avec la scène principale
            ThemeManager.getInstance().setMainScene(mainScene);
        }

        // Affiche la scène principale : le calendrier
        AppManager.showScene("calendar-view.fxml", null);
    }

    // Méthode principale (point d'entrée standard)
    public static void main(String[] args) {
        launch(); // Lance l'application JavaFX (déclenche start(...))

        // Arrête le timer à la fin de l'application (nettoyage)
        Timer.stopTimer();
    }
}
