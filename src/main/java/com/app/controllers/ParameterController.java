package com.app.controllers;

import com.app.AppManager;
import com.app.models.Database;
import com.app.models.PeriodType;
import com.app.models.User;
import com.app.utils.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
    public VBox deleteButtonContainer;

    // Composants de l'interface utilisateur
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<PeriodType> labelComboBox;
    @FXML
    private ComboBox<TimeStamp> frequencyComboBox;
    @FXML
    private ComboBox<TimeStamp> durationComboBox;
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

    private static class TimeStamp {
        private final Duration duration;

        public TimeStamp(Duration duration) {
            this.duration = duration;
        }

        public Duration getDuration() {
            return duration;
        }

        @Override
        public String toString() {
            long hours = duration.toHours();
            long minutes = duration.minusHours(hours).toMinutes() % 60;
            long seconds = duration.minusHours(hours).toSeconds() % 60;

            StringBuilder stringBuilder = new StringBuilder();
            if (hours != 0) {
                stringBuilder.append(String.format("%d heures ", hours));
            }
            if (minutes != 0) {
                stringBuilder.append(String.format("%d minutes", minutes));
            }
            if (seconds != 0) {
                stringBuilder.append(String.format("%d secondes", seconds));
            }
            return stringBuilder.toString();
        }
    }

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

        // Désactiver le bouton Créer si le champ texte est vide
        createLabelButton.disableProperty().bind(
                labelTextField.textProperty().isEmpty()
        );

        // Désactiver le bouton Supprimer si aucune étiquette n'est sélectionnée
        deleteLabelButton.disableProperty().bind(
                deleteLabelComboBox.valueProperty().isNull()
        );

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
        frequencyComboBox.getItems().addAll(
                new TimeStamp(Duration.ofSeconds(30)),
                new TimeStamp(Duration.ofMinutes(30)),
                new TimeStamp(Duration.ofHours(1)),
                new TimeStamp(Duration.ofHours(2)),
                new TimeStamp(Duration.ofHours(4))
        );

        // Durées de pause
        durationComboBox.getItems().addAll(
                new TimeStamp(Duration.ofSeconds(10)),
                new TimeStamp(Duration.ofMinutes(5)),
                new TimeStamp(Duration.ofMinutes(10)),
                new TimeStamp(Duration.ofMinutes(15)),
                new TimeStamp(Duration.ofMinutes(30))
        );

    }

    /**
     * Gestion de la sélection d'un PeriodType
     */
    @FXML
    private void onPeriodTypeSelected() {
        PeriodType selectedType = labelComboBox.getValue();
        if (selectedType != null) {
            frequencyComboBox.setValue(new TimeStamp(selectedType.getPauseContainer().getFrequency()));
            durationComboBox.setValue(new TimeStamp(selectedType.getPauseContainer().getLength()));

            // Indicateur visuel que les valeurs sont chargées
            confirmConfigButton.setText("Modifié");
            confirmConfigButton.setStyle("-fx-background-color: #95a5a6;");

            // Réinitialiser après 1 seconde
            Timeline timeline = new Timeline(new KeyFrame(
                    javafx.util.Duration.seconds(1),
                    ae -> {
                        confirmConfigButton.setText("Confirmer");
                        confirmConfigButton.setStyle("");
                    }));
            timeline.play();
        }
    }

    /**
     * Sauvegarder la configuration de notification
     */
    @FXML
    private void saveNotificationConfig() {
        PeriodType selectedType = labelComboBox.getValue();
        Duration frequency = frequencyComboBox.getValue().getDuration();
        Duration length = durationComboBox.getValue().getDuration();

        if (selectedType != null && frequency != null && length != null) {
            selectedType.getPauseContainer().setLength(length);
            selectedType.getPauseContainer().setFrequency(frequency);

            AppManager.showAlert(Alert.AlertType.INFORMATION, "Configuration sauvegardée",
                    "Les paramètres de notification ont été mis à jour.");
        } else {
            AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez sélectionner tous les paramètres.");
        }
        confirmConfigButton.setText("✓ Confirmé");
        confirmConfigButton.setStyle("-fx-background-color: -secondary-color;");

        Timeline timeline = new Timeline(new KeyFrame(
                javafx.util.Duration.seconds(1.5),
                ae -> {
                    confirmConfigButton.setText("Confirmer");
                    confirmConfigButton.setStyle("");
                }));
        timeline.play();
    }

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
            // Changement visuel pour indiquer la réussite
            nameField.getStyleClass().add("validated");
            // Après 2 secondes, retirer la classe
            Timeline timeline = new Timeline(new KeyFrame(
                    javafx.util.Duration.seconds(2),
                    ae -> nameField.getStyleClass().remove("validated")));
            timeline.play();

            // Récupérer l'utilisateur actuel
            User currentUser = Database.getConnectedUser();

            if (currentUser != null) {
                // Mise à jour du nom de l'utilisateur
                currentUser.setUsername(newName);

                AppManager.showAlert(Alert.AlertType.INFORMATION, "Mise à jour effectuée",
                        "Votre nom a été modifié avec succès.");
            } else {
                AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Aucun utilisateur connecté.");
            }
        } else {
            AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
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

                AppManager.showAlert(Alert.AlertType.INFORMATION, "Mise à jour effectuée",
                        "Votre mot de passe a été modifié avec succès.");
                passwordField.clear();
            } else {
                AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Aucun utilisateur connecté.");
            }
        } else {
            AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer un mot de passe valide");
        }
    }

    /**
     * Création d'une nouvelle étiquette
     */
    @FXML
    private void createPeriodType() {
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
            AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer des valeurs numériques valides pour l'objectif de temps.");
            return;
        }

        // Vérifier les valeurs saisies
        if (hours < 0 || minutes < 0 || minutes > 59) {
            AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer des valeurs valides pour l'objectif de temps (heures ≥ 0, 0 ≤ minutes ≤ 59).");
            return;
        }

        // Créer l'objectif de temps en combinant heures et minutes
        Duration timeObjective = Duration.ofHours(hours).plusMinutes(minutes);

        if (!labelText.isEmpty() && color != null) {
            User currentUser = Database.getConnectedUser();
            if (currentUser != null) {
                //  Créer un nouveau PeriodType avec l'objectif de temps défini par l'utilisateur

                boolean success = null != Database.addPeriodTypeToUser(labelText, color, timeObjective, currentUser);

                if (success) {
                    AppManager.showAlert(Alert.AlertType.INFORMATION, "Création réussie",
                            "L'étiquette '" + labelText + "' a été créée avec succès.");

                    // Réinitialisation des champs
                    labelTextField.clear();
                    colorPicker.setValue(Color.web("#1E90FF")); // Remettre à la couleur par défaut
                    objectiveHoursField.clear();
                    objectiveMinutesField.clear();
                } else {
                    AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Impossible de créer l'étiquette. Veuillez réessayer.");
                }
            }
        } else {
            AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Veuillez entrer un nom d'étiquette valide et choisir une couleur.");
        }
    }

    /**
     * Suppression d'une étiquette
     */
    @FXML
    private void deletePeriodType() {
        PeriodType typeToDelete = deleteLabelComboBox.getValue();
        if (typeToDelete != null) {
            User currentUser = Database.getConnectedUser();
            if (currentUser != null) {
                // Confirmation de suppression
                boolean confirmed = AppManager.showConfirmation("Confirmation de suppression",
                        "Êtes-vous sûr de vouloir supprimer l'étiquette '" + typeToDelete.getTitle() + "' ?");

                if (confirmed) {
                    // Supprimer le PeriodType
                    boolean success = Database.removePeriodTypeFromUser(typeToDelete, currentUser);

                    if (success) {
                        AppManager.showAlert(Alert.AlertType.INFORMATION, "Suppression réussie",
                                "L'étiquette a été supprimée avec succès.");
                    } else {
                        AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
                                "Impossible de supprimer l'étiquette. Veuillez réessayer.");
                    }
                }
            }
        } else {
            AppManager.showAlert(Alert.AlertType.ERROR, "Erreur",
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
        boolean confirmed = AppManager.showConfirmation("Confirmation de déconnexion",
                "Êtes-vous sûr de vouloir vous déconnecter ?");

        if (confirmed) {
            Database.disconnectUser();
            AppManager.showScene("connection-view.fxml", null);
        }
    }

    /**
     * Suppression du compte utilisateur
     */
    @FXML
    private void deleteAccount() {
        boolean confirmed = AppManager.showConfirmation("Confirmation de suppression",
                "Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.");

        if (confirmed) {
            User disconnectedUser = Database.disconnectUser();
            Database.deleteUser(disconnectedUser);
            AppManager.showScene("connection-view.fxml", null);
        }
    }

}