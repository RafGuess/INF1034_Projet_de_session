package com.app.controllers;

import com.app.utils.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue des paramètres de l'application
 */
public class ParameterController implements Initializable {

    // Composants de l'interface utilisateur
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> labelComboBox;
    @FXML
    private ComboBox<String> frequencyComboBox;
    @FXML
    private ComboBox<String> durationComboBox;
    @FXML
    private TextField labelTextField;
    @FXML
    private ComboBox<String> deleteLabelComboBox;
    @FXML
    private ToggleButton themeToggle;

    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button confirmConfigButton;


    // État de l'application
    private boolean isDarkTheme = false;

    /**
     * Initialisation du contrôleur
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des listes déroulantes
        initializeComboBoxes();

        // Chargement des paramètres utilisateur
        loadUserSettings();

        // Lier le toggle au ThemeManager
        themeToggle.selectedProperty().bindBidirectional(
                ThemeManager.getInstance().darkModeProperty());

        // Initialiser le ColorPicker avec une couleur par défaut
        colorPicker.setValue(javafx.scene.paint.Color.web("#1E90FF"));

    }

    /**
     * Initialisation des options pour les listes déroulantes
     */
    private void initializeComboBoxes() {
        // Étiquettes
        labelComboBox.getItems().addAll("Travail", "Pause", "Réunion", "Personnel");

        // Fréquences de pause
        frequencyComboBox.getItems().addAll("30 minutes", "1 heure", "2 heures", "4 heures");

        // Durées de pause
        durationComboBox.getItems().addAll("5 minutes", "10 minutes", "15 minutes", "30 minutes");


        // Liste des étiquettes pour suppression
        deleteLabelComboBox.getItems().addAll("Travail", "Pause", "Réunion", "Personnel");
    }

    /**
     * Gestion de la sélection d'un PeriodType
     */
    @FXML
    private void onPeriodTypeSelected() {
        String selectedType = labelComboBox.getValue();
        if (selectedType != null) {

            // À revoir
            String frequency = "1 heure";
            String duration = "15 minutes";

            frequencyComboBox.setValue(frequency);
            durationComboBox.setValue(duration);
        }
    }

    /**
     * Sauvegarder la configuration de notification
     */
    @FXML
    private void saveNotificationConfig() {
        String selectedType = labelComboBox.getValue();
        String frequency = frequencyComboBox.getValue();
        String duration = durationComboBox.getValue();

        if (selectedType != null && frequency != null && duration != null) {
            // Enregistrer les modifications (à implémenter avec votre modèle de données)

            showAlert(Alert.AlertType.INFORMATION, "Configuration sauvegardée",
                    "Les paramètres de notification ont été mis à jour.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez sélectionner tous les paramètres.");
        }
    }


    // Accéssibilité


    /**
     * Chargement des paramètres utilisateur depuis le service
     */
    private void loadUserSettings() {
        // Définir un nom par défaut
        nameField.setText("Utilisateur");

        //  Configurer le thème
        isDarkTheme = ThemeManager.getInstance().isDarkMode();
        themeToggle.setSelected(isDarkTheme);
    }

    /**
     * Mise à jour du nom de l'utilisateur
     */
    @FXML
    private void updateName() {
        String newName = nameField.getText().trim();
        if (!newName.isEmpty()) {
            // Mise à jour du nom dans le service (à implémenter)
            showAlert(Alert.AlertType.INFORMATION, "Mise à jour effectuée",
                    "Votre nom a été modifié avec succès.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer un nom valide.");
        }
    }

    /**
     * Mise à jour du mot de passe
     */
    @FXML
    private void updatePassword() {
        String newPassword = passwordField.getText().trim();
        if (!newPassword.isEmpty()) {
            // Mise à jour du mot de passe dans le service (à implémenter)
            showAlert(Alert.AlertType.INFORMATION, "Mise à jour effectuée",
                    "Votre mot de passe a été modifié avec succès.");
            passwordField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer un mot de passe valide.");
        }
    }

    /**
     * Création d'une nouvelle étiquette
     */
    @FXML
    private void createLabel() {
        String labelText = labelTextField.getText().trim();
        javafx.scene.paint.Color color = colorPicker.getValue();

        if (!labelText.isEmpty() && color != null) {
            // Convertir la couleur en code hexadécimal
            String colorValue = String.format("#%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));

            // Ajout de l'étiquette (à implémenter avec un service)
            labelComboBox.getItems().add(labelText);
            deleteLabelComboBox.getItems().add(labelText);

            showAlert(Alert.AlertType.INFORMATION, "Création réussie",
                    "L'étiquette '" + labelText + "' a été créée avec succès.");

            // Réinitialisation des champs
            labelTextField.clear();
            colorPicker.setValue(javafx.scene.paint.Color.web("#1E90FF")); // Remettre à la couleur par défaut
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer un nom d'étiquette valide et choisir une couleur.");
        }
    }

    /**
     * Suppression d'une étiquette
     */
    @FXML
    private void deleteLabel() {
        String labelToDelete = deleteLabelComboBox.getValue();
        if (labelToDelete != null) {
            // Confirmation de suppression
            boolean confirmed = showConfirmation("Confirmation de suppression",
                    "Êtes-vous sûr de vouloir supprimer l'étiquette '" + labelToDelete + "' ?");

            if (confirmed) {
                // Suppression de l'étiquette des listes
                labelComboBox.getItems().remove(labelToDelete);
                deleteLabelComboBox.getItems().remove(labelToDelete);
                deleteLabelComboBox.setValue(null);

                showAlert(Alert.AlertType.INFORMATION, "Suppression réussie",
                        "L'étiquette a été supprimée avec succès.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez sélectionner une étiquette à supprimer.");
        }
    }

    /**
     * Basculement entre thème clair et sombre
     */
    @FXML
    private void toggleTheme() {
        isDarkTheme = themeToggle.isSelected();
        ThemeManager.getInstance().setDarkMode(isDarkTheme);
    }

    /**
     * Déconnexion de l'utilisateur
     */
    @FXML
    private void logout() {
        boolean confirmed = showConfirmation("Confirmation de déconnexion",
                "Êtes-vous sûr de vouloir vous déconnecter ?");

        if (confirmed) {
            // Code pour la déconnexion (à implémenter)
            System.out.println("Déconnexion en cours...");

            // Exemple : fermer la fenêtre actuelle
            Stage stage = (Stage) themeToggle.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Suppression du compte utilisateur
     */
    @FXML
    private void deleteAccount() {
        boolean confirmed = showConfirmation("Confirmation de suppression",
                "Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.");

        if (confirmed) {
            // Code pour la suppression du compte (à implémenter)
            System.out.println("Suppression du compte en cours...");

            // Exemple : fermer l'application
            Stage stage = (Stage) themeToggle.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Affichage de l'aide
     */
    @FXML
    private void showHelp() {
        showAlert(Alert.AlertType.INFORMATION, "Aide",
                "Cette page vous permet de configurer votre profil utilisateur et les paramètres de l'application.");
    }

    /**
     * Affichage des paramètres (déjà sur cette page)
     */
    @FXML
    private void showSettings() {
        // Déjà sur la page des paramètres
        showAlert(Alert.AlertType.INFORMATION, "Paramètres",
                "Vous êtes déjà sur la page des paramètres.");
    }

    /**
     * Affiche une alerte avec le type, titre et message spécifiés
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue de confirmation
     *
     * @return true si l'utilisateur a confirmé, false sinon
     */
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType buttonTypeOk = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        return alert.showAndWait().orElse(buttonTypeCancel) == buttonTypeOk;
    }
}