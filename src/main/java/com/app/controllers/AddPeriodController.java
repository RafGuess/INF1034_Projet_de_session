package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.factories.AddPeriodFactory;
import com.app.models.Database;
import com.app.models.PeriodType;
import com.app.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AddPeriodController {

    // FXML : éléments liés à l'interface utilisateur
    @FXML private ComboBox<PeriodType> periodTypeComboBox; // sélection du type de période
    @FXML private DatePicker periodDatePicker; // sélection de la date
    @FXML private ComboBox<String> startPeriodHourComboBox; // heure de début
    @FXML private ComboBox<String> startPeriodMinuteComboBox; // minute de début
    @FXML private ComboBox<String> endPeriodHourComboBox; // heure de fin
    @FXML private ComboBox<String> endPeriodMinuteComboBox; // minute de fin
    @FXML private ComboBox<User> collaboratorsComboBox; // sélection de collaborateurs
    @FXML private VBox collaboratorsVBox; // affichage des collaborateurs sélectionnés
    @FXML private TextArea notesTextArea; // champ texte pour les notes
    @FXML private Label warningLabel; // message d'erreur/avertissement

    private final List<User> collaborators = new ArrayList<>(); // liste des collaborateurs sélectionnés

    final private AddPeriodFactory addPeriodFactory = new AddPeriodFactory(); // fabrique pour remplir les composants

    public void initialize() {
        // Remplit les ComboBox avec les données appropriées
        addPeriodFactory.populatePeriodTypesComboBox(periodTypeComboBox);
        addPeriodFactory.populateHoursComboBox(startPeriodHourComboBox);
        addPeriodFactory.populateMinutesComboBox(startPeriodMinuteComboBox);
        addPeriodFactory.populateHoursComboBox(endPeriodHourComboBox);
        addPeriodFactory.populateMinutesComboBox(endPeriodMinuteComboBox);
        addPeriodFactory.populateUserComboBox(collaboratorsComboBox);
        addPeriodFactory.updateCollaboratorsLabel(collaboratorsVBox, collaborators); // initialise l'affichage
    }

    @FXML
    public void onSavePeriod(ActionEvent event) {
        // Récupération des valeurs entrées par l'utilisateur
        PeriodType periodType = periodTypeComboBox.getValue();
        LocalDate periodDate = periodDatePicker.getValue();
        String notes = notesTextArea.getText();

        LocalTime periodStartTime;
        LocalTime periodEndTime;

        try {
            // Tente de construire les LocalTime à partir des valeurs choisies
            periodStartTime = LocalTime.of(Integer.parseInt(startPeriodHourComboBox.getValue()), Integer.parseInt(startPeriodMinuteComboBox.getValue()));
            periodEndTime = LocalTime.of(Integer.parseInt(endPeriodHourComboBox.getValue()), Integer.parseInt(endPeriodMinuteComboBox.getValue()));
        } catch (NullPointerException | NumberFormatException e) {
            // Si une des valeurs est manquante
            periodStartTime = null;
            periodEndTime = null;
        }

        // Vérifie que tous les champs sont remplis
        if (periodType == null || periodDate == null || periodStartTime == null || periodEndTime == null) {
            warningLabel.setText("Champs incomplets");
            return;
        }

        // Vérifie que l'heure de début est avant l'heure de fin
        if (periodStartTime.isAfter(periodEndTime)) {
            warningLabel.setText("L'heure de début est après l'heure de fin.");
            return;
        }

        // Ajoute l'utilisateur connecté à la liste des collaborateurs
        collaborators.add(Database.getConnectedUser());

        // Tente d'ajouter la période dans la base de données
        User unavailableUser = Database.addPeriod(periodDate, periodStartTime, periodEndTime, periodType, notes, collaborators);

        if (unavailableUser != null) {
            // Gestion des conflits d'horaire
            if (unavailableUser.equals(Database.getConnectedUser())) {
                warningLabel.setText("Une période est déjà configuré à ce moment.");
            } else {
                warningLabel.setText("L'utilisateur " + unavailableUser + " est indisponible.");
            }
            return;
        }

        collaborators.clear(); // Vide la liste après sauvegarde réussie
        closeWindow(event); // Ferme la fenêtre
    }

    @FXML
    public void onCancelPeriodCreation(ActionEvent event) {
        closeWindow(event); // Ferme la fenêtre sans sauvegarder
    }

    @FXML
    public void onModifyCollaborators() {
        // Ajoute ou retire un collaborateur sélectionné
        User collaborator = collaboratorsComboBox.getValue();
        if (collaborator == null) return;

        boolean contained = collaborators.remove(collaborator);
        if (!contained) {
            collaborators.add(collaboratorsComboBox.getValue());
        }
        // Met à jour le label avec la liste actuelle des collaborateurs
        addPeriodFactory.updateCollaboratorsLabel(collaboratorsVBox, collaborators);
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        // Ferme la fenêtre actuelle en récupérant la Stage depuis l'événement
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}
