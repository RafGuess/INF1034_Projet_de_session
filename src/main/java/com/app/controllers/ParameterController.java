package com.app.controllers;

import com.app.models.Database;
import com.app.models.PeriodType;
import com.app.models.User;
import com.app.utils.ThemeManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue des paramètres de l'application
 */
public class ParameterController implements Initializable {

    public Button validateNameButton;
    public Button validatePasswordButton;
    public Button createLabelButton;
    public Button deleteLabelButton;
    public Button logoutButton;
    public Button deleteAccountButton;
    public HBox createButtonContainer;
    public VBox deleteButtonContainer;

    // Composants de l'interface utilisateur
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<PeriodType> labelComboBox;
    @FXML
    private ComboBox<String> frequencyComboBox;
    @FXML
    private ComboBox<String> durationComboBox;
    @FXML
    private TextField labelTextField;
    @FXML
    private ComboBox<PeriodType> deleteLabelComboBox;
    @FXML
    private ToggleButton themeToggle;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button confirmConfigButton;
    @FXML
    private TextField objectiveHoursField;
    @FXML
    private TextField objectiveMinutesField;


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
        // Charger les types de période (étiquettes) de l'utilisateur connecté
        User currentUser = Database.getConnectedUser();
        if (currentUser != null) {
            // Récupérer les PeriodTypes de l'utilisateur connecté
            ObservableList<PeriodType> periodTypes = Database.getPeriodTypesOfUser(currentUser);

            // Remplir les ComboBox avec les PeriodTypes
            labelComboBox.setItems(periodTypes);
            deleteLabelComboBox.setItems(periodTypes);
        }
        // Fréquences de pause
        frequencyComboBox.getItems().addAll("30 minutes", "1 heure", "2 heures", "4 heures");

        // Durées de pause
        durationComboBox.getItems().addAll("5 minutes", "10 minutes", "15 minutes", "30 minutes");

    }

    /**
     * Gestion de la sélection d'un PeriodType
     */
    @FXML
    private void onPeriodTypeSelected() {
        PeriodType selectedType = labelComboBox.getValue();
        if (selectedType != null) {
            //valeurs par défaut
            frequencyComboBox.setValue("1 heure");
            durationComboBox.setValue("15 minutes");
        }
    }

    /**
     * Sauvegarder la configuration de notification
     */
    @FXML
    private void saveNotificationConfig() {
        PeriodType selectedType = labelComboBox.getValue();
        String frequency = frequencyComboBox.getValue();
        String duration = durationComboBox.getValue();

        if (selectedType != null && frequency != null && duration != null) {
            // message de succès

            showAlert(Alert.AlertType.INFORMATION, "Configuration sauvegardée",
                    "Les paramètres de notification ont été mis à jour.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez sélectionner tous les paramètres.");
        }
    }

    // Accéssibilité
    // À ne plus implémenter


    /**
     * Chargement des paramètres utilisateur depuis le service
     */
    private void loadUserSettings() {

        // Récupérer l'utilisateur actuel
        User currentUser = Database.getConnectedUser();
        if (currentUser != null) {
            // Charger le nom de l'utilisateur
            nameField.setText(currentUser.getUsername());
        } else {
            nameField.setText("Utilisateur");
        }

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
            // Récupérer l'utilisateur actuel
            User currentUser = Database.getConnectedUser();

            if (currentUser != null) {
                // Mise à jour du nom de l'utilisateur
                currentUser.setUsername(newName);

                showAlert(Alert.AlertType.INFORMATION, "Mise à jour effectuée",
                        "Votre nom a été modifié avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Aucun utilisateur connecté.");
            }
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
            // Récupérer l'utilisateur actuel
            User currentUser = Database.getConnectedUser();

            if (currentUser != null) {
                // Mise à jour du mot de passe
                currentUser.setPassword(newPassword);

                showAlert(Alert.AlertType.INFORMATION, "Mise à jour effectuée",
                        "Votre mot de passe a été modifié avec succès.");
                passwordField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Aucun utilisateur connecté.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer un mot de passe valide");
        }
    }

    /**
     * Création d'une nouvelle étiquette
     */
    @FXML
    private void createLabel() {
        String labelText = labelTextField.getText().trim();
        Color color = colorPicker.getValue();

        // Récupérer les valeurs d'objectif de temps
        int hours = 0;
        int minutes = 0;

        try {
            // Convertir les champs en entiers si remplis
            if (!objectiveHoursField.getText().trim().isEmpty()) {
                hours = Integer.parseInt(objectiveHoursField.getText().trim());
            }

            if (!objectiveMinutesField.getText().trim().isEmpty()) {
                minutes = Integer.parseInt(objectiveMinutesField.getText().trim());
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer des valeurs numériques valides pour l'objectif de temps.");
            return;
        }

        // Vérifier les valeurs saisies
        if (hours < 0 || minutes < 0 || minutes > 59) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer des valeurs valides pour l'objectif de temps (heures ≥ 0, 0 ≤ minutes ≤ 59).");
            return;
        }

        // Créer l'objectif de temps en combinant heures et minutes
        Duration timeObjective = Duration.ofHours(hours).plusMinutes(minutes);

        if (!labelText.isEmpty() && color != null) {
            User currentUser = Database.getConnectedUser();
            if (currentUser != null) {
                //  Créer un nouveau PeriodType avec l'objectif de temps défini par l'utilisateur

                boolean success = Database.addPeriodTypeToUser(labelText, color, timeObjective, currentUser);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Création réussie",
                            "L'étiquette '" + labelText + "' a été créée avec succès.");

                    // Réinitialisation des champs
                    labelTextField.clear();
                    colorPicker.setValue(Color.web("#1E90FF")); // Remettre à la couleur par défaut
                    objectiveHoursField.clear();
                    objectiveMinutesField.clear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Impossible de créer l'étiquette. Veuillez réessayer.");
                }
            }
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
        PeriodType typeToDelete = deleteLabelComboBox.getValue();
        if (typeToDelete != null) {
            User currentUser = Database.getConnectedUser();
            if (currentUser != null) {
                // Confirmation de suppression
                boolean confirmed = showConfirmation("Confirmation de suppression",
                        "Êtes-vous sûr de vouloir supprimer l'étiquette '" + typeToDelete.getTitle() + "' ?");

                if (confirmed) {
                    // Supprimer le PeriodType
                    boolean success = Database.removePeriodTypeFromUser(typeToDelete, currentUser);

                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Suppression réussie",
                                "L'étiquette a été supprimée avec succès.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur",
                                "Impossible de supprimer l'étiquette. Veuillez réessayer.");
                    }
                }
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
            //System.out.println("Déconnexion en cours...");

            // Juste fermer la fenêtre actuelle car on a pas fait de connexion à une BD
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
            // System.out.println("Suppression du compte en cours...");

            // Juste fermer la fenêtre actuelle car on a pas fait de connexion à une BD
            Stage stage = (Stage) themeToggle.getScene().getWindow();
            stage.close();
        }
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