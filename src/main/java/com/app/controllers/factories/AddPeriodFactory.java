package com.app.controllers.factories;

import com.app.models.Database;
import com.app.models.PeriodType;
import com.app.models.User;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.List;
import java.util.stream.IntStream;

 //Classe utilitaire pour peupler différents éléments de l'interface utilisateur liés à l'ajout d'une période.
public class AddPeriodFactory {

    // Remplit une ComboBox avec les types de périodes disponibles pour l'utilisateur connecté
    public void populatePeriodTypesComboBox(ComboBox<PeriodType> comboBox) {
        // Récupère les types de périodes de l'utilisateur actuellement connecté et les ajoute à la ComboBox
        comboBox.setItems(Database.getPeriodTypesOfUser(Database.getConnectedUser()));
    }

    // Remplit une ComboBox avec les heures de 0 à 23
    public void populateHoursComboBox(ComboBox<Integer> comboBox) {
        // Crée une liste contenant les entiers de 0 à 23 et la définit comme contenu de la ComboBox
        comboBox.setItems(FXCollections.observableArrayList(
                IntStream.range(0, 24).boxed().toList() // .boxed() convertit les int en Integer
        ));
    }

    // Remplit une ComboBox avec les minutes de 0 à 59
    public void populateMinutesComboBox(ComboBox<Integer> comboBox) {
        // Crée une liste contenant les entiers de 0 à 59 et la définit comme contenu de la ComboBox
        comboBox.setItems(FXCollections.observableArrayList(
                IntStream.range(0, 60).boxed().toList()
        ));
    }

    // Remplit une ComboBox avec tous les utilisateurs sauf l'utilisateur connecté
    public void populateUserComboBox(ComboBox<User> comboBox) {
        // Récupère tous les utilisateurs et filtre pour exclure l'utilisateur connecté, puis les ajoute à la ComboBox
        comboBox.setItems(Database.getUsers().filtered(user ->
                !user.equals(Database.getConnectedUser()) // exclut l'utilisateur connecté
        ));
    }

    // Met à jour le texte d'un Label pour afficher la liste des collaborateurs sélectionnés
    public void updateCollaboratorsLabel(Label collaboratorsLabel, List<User> collaborators) {
        // Convertit la liste de collaborateurs en chaîne de caractères et l'affiche dans le Label
        collaboratorsLabel.setText(collaborators.toString());
    }
}
