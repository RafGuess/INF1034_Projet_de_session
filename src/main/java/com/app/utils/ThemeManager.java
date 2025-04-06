package com.app.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;

/**
 * Gestionnaire de thème pour toute l'application
 */
public class ThemeManager {
    private static ThemeManager instance;
    private final BooleanProperty darkMode = new SimpleBooleanProperty(false);
    private Scene mainScene;

    private ThemeManager() {
        // Initialisation du gestionnaire
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void setMainScene(Scene scene) {
        this.mainScene = scene;
        // Appliquer le thème initial
        applyTheme(darkMode.get());
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public boolean isDarkMode() {
        return darkMode.get();
    }

    public void setDarkMode(boolean isDark) {
        this.darkMode.set(isDark);
        applyTheme(isDark);
    }

    private void applyTheme(boolean isDark) {
        if (mainScene == null) return;

        if (isDark) {
            mainScene.getRoot().getStyleClass().add("dark-theme");
            mainScene.getRoot().getStyleClass().remove("light-theme");
        } else {
            mainScene.getRoot().getStyleClass().add("light-theme");
            mainScene.getRoot().getStyleClass().remove("dark-theme");
        }
    }
}