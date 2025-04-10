package com.app.controllers;

import com.app.controllers.controllerInterfaces.DataInitializable;
import com.app.models.Period;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShowPeriodController implements DataInitializable<Period> {

    // Période à afficher dans l'interface
    private Period shownPeriod;

    @FXML private Label periodLabel;

    // Méthode appelée automatiquement lors du chargement du FXML avec des données
    @Override
    public void initializeWithData(Period period) {
        shownPeriod = period; // Stocke la période reçue pour l'utiliser dans la vue
        periodLabel.setText(period.toString());
    }
}
