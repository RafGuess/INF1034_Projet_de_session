package com.app.controllers;

import com.app.AppManager;
import com.app.models.Database;
import com.app.models.User;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ConnectionController {

    @FXML public TextField userNameTextField;
    @FXML public TextField passwordTextField;
    @FXML public Label warningLabel;
    @FXML public VBox usersVBox;

    public void initialize() {
        // Pour faciliter les tests, montrent la liste de tous les utilisateurs dans l'écran de connexion
        for (User user : Database.getUsers()) {
            Label label = new Label( "Nom utilisateur: "+ user.getUsername() + "   Mot de passe: " + user.getPassword());
            usersVBox.getChildren().add(label);
        }
    }

    @FXML
    public void onConnectionButtonClicked() {
        // Connecte l'utilisateur si le bouton est cliqué et que les informations sont bonnes
        boolean success = Database.connectUser(userNameTextField.getText(), passwordTextField.getText());
        if (!success) {
            warningLabel.setText("La connexion a échouée, les informations sont incorrectes.");
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> warningLabel.setText(""));
            delay.play();
        } else {
            AppManager.showScene("calendar-view.fxml", null);
        }
    }
}
