package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.factories.MenuBarFactory;
import com.app.utils.ThemeManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;

public class MenuBarController {

    // Boutons du menu de navigation principal
    @FXML
    private Button calendarButton;
    @FXML
    private Button tasksButton;
    @FXML
    private Button statisticsButton;
    @FXML
    private Button parametersButton;

    // Factory utilisée pour redimensionner les boutons selon la taille de la fenêtre
    private final MenuBarFactory menuBarFactory = new MenuBarFactory(
            AppManager.getWidthProperty(), AppManager.getHeightProperty()
    );

    public void initialize() {
        // Redimensionne les boutons du menu en fonction de la largeur de l'application
        menuBarFactory.resizeMenuButtons(calendarButton, tasksButton, statisticsButton, parametersButton);

        // Empêche les boutons de recevoir automatiquement le focus (évite contour bleu lors du démarrage)
        calendarButton.setFocusTraversable(false);
        tasksButton.setFocusTraversable(false);
        statisticsButton.setFocusTraversable(false);
        parametersButton.setFocusTraversable(false);
    }

    // Gestionnaire du clic sur le bouton calendrier
    @FXML
    public void onCalendarButtonClicked() {
        // Si la vue actuelle n'est pas déjà celle du calendrier, la charger
        if (!AppManager.getCurrentView().equals("calendar-view.fxml")) {
            AppManager.showScene("calendar-view.fxml", null);
        }
    }

    // Gestionnaire du clic sur le bouton tâches
    @FXML
    public void onTasksButtonClicked() {

        // Créer une alerte de type information
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fonctionnalité non disponible");
        alert.setHeaderText("Menu Tâches non implémenté");
        alert.setContentText("Cette fonctionnalité n'est pas encore disponible. Veuillez utiliser les autres menus de l'application.");

        // Récupérer le DialogPane de l'alerte
        DialogPane dialogPane = alert.getDialogPane();

        // Appliquer le thème
        Scene scene = tasksButton.getScene();
        if (scene != null) {
            dialogPane.getStylesheets().addAll(scene.getStylesheets());

            // Appliquer la classe de style appropriée selon le thème
            if (ThemeManager.getInstance().isDarkMode()) {
                dialogPane.getStyleClass().add("dark-theme");
            } else {
                dialogPane.getStyleClass().add("light-theme");
            }
        }
        // Afficher l'alerte et attendre que l'utilisateur la ferme
        alert.showAndWait();

        // if (!AppManager.getCurrentView().equals("tasks-view.fxml")) {
        //    AppManager.showScene("tasks-view.fxml", null);
        //}
    }

    // Gestionnaire du clic sur le bouton statistiques
    @FXML
    public void onStatisticsButtonClicked() {
        if (!AppManager.getCurrentView().equals("statistics-view.fxml")) {
            AppManager.showScene("statistics-view.fxml", null);
        }
    }

    // Gestionnaire du clic sur le bouton paramètres
    @FXML
    public void onParametersButtonClicked() {
        if (!AppManager.getCurrentView().equals("parameters-view.fxml")) {
            AppManager.showScene("parameters-view.fxml", null);
        }
    }
}
