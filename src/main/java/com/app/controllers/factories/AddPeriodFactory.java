package com.app.controllers.factories;

import com.app.models.Database;
import com.app.models.PeriodType;
import com.app.models.User;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.List;
import java.util.stream.IntStream;

public class AddPeriodFactory {

    public void populatePeriodTypesComboBox(ComboBox<PeriodType> comboBox) {
        comboBox.setItems(Database.getPeriodTypesOfUser(Database.getConnectedUser()));
    }

    public void populateHoursComboBox(ComboBox<Integer> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList(IntStream.range(0,24).boxed().toList()));
    }

    public void populateMinutesComboBox(ComboBox<Integer> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList(IntStream.range(0,60).boxed().toList()));
    }

    public void populateUserComboBox(ComboBox<User> comboBox) {
        comboBox.setItems(Database.getUsers().filtered(user -> !user.equals(Database.getConnectedUser())));
    }

    public void updateCollaboratorsLabel(Label collaboratorsLabel, List<User> collaborators) {
        collaboratorsLabel.setText(collaborators.toString());
    }
}
