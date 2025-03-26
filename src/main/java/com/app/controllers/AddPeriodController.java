package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.factories.AddPeriodFactory;
import com.app.models.Database;
import com.app.models.PeriodType;
import com.app.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AddPeriodController {
    @FXML private ComboBox<PeriodType> periodTypeComboBox;
    @FXML private DatePicker periodDatePicker;
    @FXML private ComboBox<Integer> startPeriodHourComboBox;
    @FXML private ComboBox<Integer> startPeriodMinuteComboBox;
    @FXML private ComboBox<Integer> endPeriodHourComboBox;
    @FXML private ComboBox<Integer> endPeriodMinuteComboBox;
    @FXML private ComboBox<User> collaboratorsComboBox;
    @FXML private Label collaboratorsLabel;
    @FXML private TextField notesTextField;
    @FXML private Label warningLabel;

    private final List<User> collaborators = new ArrayList<>();

    final private AddPeriodFactory addPeriodFactory = new AddPeriodFactory();

    public void initialize() {
        addPeriodFactory.populatePeriodTypesComboBox(periodTypeComboBox);
        addPeriodFactory.populateHoursComboBox(startPeriodHourComboBox);
        addPeriodFactory.populateMinutesComboBox(startPeriodMinuteComboBox);
        addPeriodFactory.populateHoursComboBox(endPeriodHourComboBox);
        addPeriodFactory.populateMinutesComboBox(endPeriodMinuteComboBox);
        addPeriodFactory.populateUserComboBox(collaboratorsComboBox);
        addPeriodFactory.updateCollaboratorsLabel(collaboratorsLabel, collaborators);
    }

    @FXML
    public void onSavePeriod() {
        PeriodType periodType = periodTypeComboBox.getValue();
        LocalDate periodDate = periodDatePicker.getValue();
        String notes = notesTextField.getText();

        LocalTime periodStartTime;
        LocalTime periodEndTime;
        try {
            periodStartTime = LocalTime.of(startPeriodHourComboBox.getValue(), startPeriodMinuteComboBox.getValue());
            periodEndTime = LocalTime.of(endPeriodHourComboBox.getValue(), endPeriodMinuteComboBox.getValue());
        } catch (NullPointerException e) {
            periodStartTime = null;
            periodEndTime = null;
        }

        if (periodType == null || periodDate == null || periodStartTime == null || periodEndTime == null) {
            warningLabel.setText("Champs incomplets");
            return;
        }

        if (periodStartTime.isAfter(periodEndTime)) {
            warningLabel.setText("L'heure de début est après l'heure de fin.");
            return;
        }

        collaborators.add(Database.getConnectedUser());

        User unavailableUser = Database.addPeriod(periodDate, periodStartTime, periodEndTime, periodType, notes, collaborators);
        if (unavailableUser != null) {
            if (unavailableUser.equals(Database.getConnectedUser())) {
                warningLabel.setText("You already have a period set at this date and time.");
            } else {
                warningLabel.setText("User " + unavailableUser + " is unavailable.");
            }
            return;
        }

        collaborators.clear();

        closeWindow();
    }

    @FXML
    public void onCancelPeriodCreation() {
        closeWindow();
    }

    @FXML
    public void onModifyCollaborators() {
        boolean contained = collaborators.remove(collaboratorsComboBox.getValue());
        if (!contained) {
            collaborators.add(collaboratorsComboBox.getValue());
        }
        addPeriodFactory.updateCollaboratorsLabel(collaboratorsLabel, collaborators);
    }

    private void closeWindow() {
        AppManager.showScene("calendar-view.fxml");
    }

}
