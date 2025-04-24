package com.app.controllers.factories;

import com.app.controllers.customNodes.PeriodNode;
import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.animation.*;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PeriodFactory {

    private final PauseTransition delay = new PauseTransition(Duration.millis(250));

    // Met à jour l'affichage des périodes visibles dans une semaine donnée
    public void updateShownPeriods(Pane periodsPane, LocalDate firstDayOfWeek, ObservableList<Period> periods,
                                   EventHandler<MouseEvent> selectedEvent, EventHandler<MouseEvent> movedEvent, boolean cancellable
    ) {
        periodsPane.getChildren().clear(); // vide le panneau avant de redessiner

        for (Period period : periods) {
            // Vérifie si la période appartient à la semaine affichée
            if (LocalDateUtils.getFirstDayOfWeek(period.getDate()).equals(firstDayOfWeek)) {
                // Crée le bouton représentant la période
                PeriodNode periodButton;
                if (cancellable) periodButton = makeCancellablePeriodButton(periodsPane, period, selectedEvent);
                else periodButton = makeMovablePeriodButton(periodsPane, period, selectedEvent, movedEvent);

                // Calcule le jour (colonne) où positionner le bouton (0 à 6)
                long i = ChronoUnit.DAYS.between(firstDayOfWeek, period.getDate());

                // Position horizontale (X) : basée sur la colonne
                periodButton.layoutXProperty().bind(
                        periodsPane.widthProperty().divide(7).multiply((double) i + 0.05)
                );

                // Position verticale (Y) : proportion de la journée écoulée
                periodButton.layoutYProperty().bind(
                        periodsPane.heightProperty().multiply( (double) period.getStartTime().toSecondOfDay() / 86400)
                );

                periodsPane.getChildren().add(periodButton); // ajoute le bouton à la vue
            }
        }
    }

    private PeriodNode makeMovablePeriodButton(Pane periodsPane, Period period,
                                               EventHandler<MouseEvent> movedEvent, EventHandler<MouseEvent> selectedEvent
    ) {
        PeriodNode periodButton = makePeriodButton(periodsPane, period);
        // Rend le boutton mobile avec un "click and drag"
        periodButton.setOnMousePressed(mouseEvent -> movablePeriodClicked(
                movedEvent, selectedEvent, mouseEvent, periodButton, periodsPane
        ));
        return periodButton;
    }

    private PeriodNode makeCancellablePeriodButton(Pane periodsPane, Period period, EventHandler<MouseEvent> cancelEvent) {
        PeriodNode periodButton = makePeriodButton(periodsPane, period);
        periodButton.getStyleClass().add("cancellable");
        // Rend le boutton mobile avec un "click and drag"
        periodButton.setOnMouseReleased(cancelEvent);
        return periodButton;
    }

    // Crée et configure un bouton (PeriodView) représentant une période
    private PeriodNode makePeriodButton(Pane periodsPane, Period period) {
        PeriodNode periodButton = new PeriodNode(period); // bouton personnalisé avec données de période

        periodButton.setText(period.getPeriodType().getTitle()); // définit le texte du bouton (titre du type de période)

        // Applique une couleur de fond personnalisée selon le type de période
        periodButton.getStyleClass().add("period-button");
        periodButton.setStyle("-fx-background-color: " + period.getPeriodType().getRGBColor() + ";");

        // Largeur : 90% de la largeur de cellule
        ObservableDoubleValue width = periodsPane.widthProperty().divide(7).multiply(0.9);
        periodButton.minWidthProperty().bind(width);
        periodButton.maxWidthProperty().bind(width);

        // Hauteur : proportion de la durée de la période par rapport à une journée (en secondes)
        ObservableDoubleValue height = periodsPane.heightProperty().multiply((double) period.getDuration().getSeconds() / 86400);
        periodButton.minHeightProperty().bind(height);
        periodButton.maxHeightProperty().bind(height);

        return periodButton; // retourne le bouton prêt à être affiché
    }

    private void movablePeriodClicked(EventHandler<MouseEvent> selectedEvent, EventHandler<MouseEvent> movedEvent,
                               MouseEvent mouseEvent, PeriodNode periodButton, Pane periodsPane
    ) {
        periodButton.offsetY = mouseEvent.getSceneY() - periodButton.getLayoutY();
        periodButton.offsetX = mouseEvent.getSceneX() - periodButton.getLayoutX();

        if (mouseEvent.getClickCount() == 2) {
            delay.stop();
            periodButton.setOnMouseReleased(selectedEvent);
        } else if (mouseEvent.getClickCount() == 1) {
            delay.setOnFinished(e -> {
                periodButton.setOnMouseReleased(movedEvent);
                periodButton.layoutYProperty().unbind();
                periodButton.layoutXProperty().unbind();
                periodButton.setOnMouseDragged(dragEvent -> movablePeriodDragged(periodButton, dragEvent, periodsPane));
            });
            delay.playFromStart();

        }
    }

    private void movablePeriodDragged(PeriodNode periodButton, MouseEvent mouseEvent, Pane periodsPane) {
        Pane parentPane = (Pane) periodButton.getParent();
        double newPosY = mouseEvent.getSceneY() - periodButton.offsetY;
        if (newPosY > 0 && newPosY < parentPane.getHeight() - periodButton.getHeight()) {
            periodButton.setLayoutY(newPosY);
        }

        double newPosX = 0;
        double minDiff = Double.MAX_VALUE;
        for (int i = 0; i < 7; i++) {
            double pos = periodsPane.getWidth()/7 * (i + 0.05);
            double diff = Math.abs(pos - mouseEvent.getSceneX() + periodButton.offsetX);
            if (diff < minDiff) {
                newPosX = pos;
                minDiff = diff;
            }
        }

        periodButton.setLayoutX(newPosX);
    }
}
