package com.app.controllers.factories;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.control.Button;


// Cette classe permet de gérer la taille des boutons du menu de navigation en fonction de la largeur totale de l'application.
public class MenuBarFactory {

    // Dimensions observables de la fenêtre principale (hauteur/largeur)
    private final ObservableDoubleValue appWidth;
    private final ObservableDoubleValue appHeight;

    // Constructeur pour injecter la largeur et la hauteur de la fenêtre
    public MenuBarFactory(ObservableDoubleValue appWidth, ObservableDoubleValue appHeight) {
        this.appWidth = appWidth;
        this.appHeight = appHeight;
    }


    // Redimensionne dynamiquement les boutons du menu en fonction de la largeur totale.
    public void resizeMenuButtons(Button calendarButton, Button tasksButton, Button statisticsButton, Button parametersButton) {
        // Chacun de ces boutons occupe 30% de la largeur de la fenêtre
        calendarButton.prefWidthProperty().bind(Bindings.multiply(appWidth, 0.3));
        tasksButton.prefWidthProperty().bind(Bindings.multiply(appWidth, 0.3));
        statisticsButton.prefWidthProperty().bind(Bindings.multiply(appWidth, 0.3));

        // Le bouton paramètres est plus petit : 10% de la largeur
        parametersButton.prefWidthProperty().bind(Bindings.multiply(appWidth, 0.1));
    }
}
