package com.app.controllers.factories;

import com.app.controllers.viewModels.PeriodView;
import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PeriodFactory {
    // Largeur observable d'une cellule du calendrier (utilisée pour positionner les périodes horizontalement)
    final private ObservableDoubleValue calendarCellWidth;

    // Constructeur prenant la largeur des cellules comme paramètre
    public PeriodFactory(ObservableDoubleValue calendarCellWidth) {
        this.calendarCellWidth = calendarCellWidth;
    }

    // Met à jour l'affichage des périodes visibles dans une semaine donnée
    public void updateShownPeriods(Pane periodsPane, LocalDate firstDayOfWeek,
                                   ObservableList<Period> periods, EventHandler<ActionEvent> buttonsActionEvent
    ) {
        periodsPane.getChildren().clear(); // vide le panneau avant de redessiner

        for (Period period : periods) {
            // Vérifie si la période appartient à la semaine affichée
            if (LocalDateUtils.getFirstDayOfWeek(period.getDate()).equals(firstDayOfWeek)) {
                // Crée le bouton représentant la période
                PeriodView periodButton = makePeriodButton(periodsPane.heightProperty(), period, buttonsActionEvent);

                // Calcule le jour (colonne) où positionner le bouton (0 à 6)
                long i = ChronoUnit.DAYS.between(firstDayOfWeek, period.getDate());

                // Position horizontale (X) : basée sur la colonne
                periodButton.layoutXProperty().bind(Bindings.multiply(calendarCellWidth, (double) i + 0.05));

                // Position verticale (Y) : proportion de la journée écoulée
                periodButton.layoutYProperty().bind(Bindings.multiply(
                        periodsPane.heightProperty(), (double) period.getStartTime().toSecondOfDay() / 86400)
                );

                periodsPane.getChildren().add(periodButton); // ajoute le bouton à la vue
            }
        }
    }

    // Crée et configure un bouton (PeriodView) représentant une période
    private PeriodView makePeriodButton(ObservableDoubleValue periodsPaneHeightProperty,
                                        Period period, EventHandler<ActionEvent> actionEvent
    ) {
        PeriodView periodButton = new PeriodView(period); // bouton personnalisé avec données de période

        periodButton.setText(period.getPeriodType().getTitle()); // définit le texte du bouton (titre du type de période)

        // Applique une couleur de fond personnalisée selon le type de période
        periodButton.setStyle("-fx-background-color: " + period.getPeriodType().getRGBColor() + ";");

        // Largeur : 90% de la largeur de cellule
        periodButton.prefWidthProperty().bind(Bindings.multiply(calendarCellWidth, 0.9));

        // Hauteur : proportion de la durée de la période par rapport à une journée (en secondes)
        periodButton.prefHeightProperty().bind(
                Bindings.multiply(periodsPaneHeightProperty, (double) period.getDuration().getSeconds() / 86400)
        );

        periodButton.setOnAction(actionEvent); // ajoute le gestionnaire d’événement

        return periodButton; // retourne le bouton prêt à être affiché
    }
}
