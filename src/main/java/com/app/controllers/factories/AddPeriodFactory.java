package com.app.controllers.factories;

import com.app.models.Database;
import com.app.models.PeriodType;
import com.app.models.User;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

 //Classe utilitaire pour peupler différents éléments de l'interface utilisateur liés à l'ajout d'une période.
public class AddPeriodFactory {

    // Remplit une ComboBox avec les types de périodes disponibles pour l'utilisateur connecté
    public void populatePeriodTypesComboBox(ComboBox<PeriodType> comboBox) {
        // Récupère les types de périodes de l'utilisateur actuellement connecté et les ajoute à la ComboBox
        comboBox.setItems(Database.getPeriodTypesOfUser(Database.getConnectedUser()));
    }

    // Remplit une ComboBox avec les heures de 0 à 23
    public void populateHoursComboBox(ComboBox<String> comboBox) {
        // Crée une liste contenant les entiers de 0 à 23 et la définit comme contenu de la ComboBox
        String[] hours = new String [24];
        for(int i =0 ; i < 24; i++){
            if (i< 10) {
                hours[i] = String.valueOf("0"+i);
            }
            else {
                hours[i] = String.valueOf(i);
            }
        }
        comboBox.setItems(FXCollections.observableArrayList(hours));
    }

    // Remplit une ComboBox avec les minutes de 0 à 59
    public void populateMinutesComboBox(ComboBox<String> comboBox) {
        // Crée une liste contenant les entiers de 0 à 59 et la définit comme contenu de la ComboBox

        String minutes [] = new String [60];
        for(int i =0 ; i < 60; i++){
            if (i< 10) {
                minutes[i] = "0" + i;
            }
            else {
                minutes[i] = String.valueOf(i);
            }
        }
        comboBox.setItems(FXCollections.observableArrayList(
                minutes
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
    public void updateCollaboratorsLabel(VBox collaboratorsVBox, List<User> collaborators) {
        // Convertit la liste de collaborateurs en chaîne de caractères et l'affiche dans le Label
        collaboratorsVBox.getChildren().clear();
        for (User collaborator : collaborators) {
            collaboratorsVBox.getChildren().add(new Label("- " + collaborator.toString()));
        }
    }
}
