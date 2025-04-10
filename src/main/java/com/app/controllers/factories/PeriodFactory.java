package com.app.controllers.factories;

import com.app.controllers.viewModels.PeriodView;
import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PeriodFactory {
    // Met à jour l'affichage des périodes visibles dans une semaine donnée
    public void updateShownPeriods(Pane periodsPane, LocalDate firstDayOfWeek,
                                   ObservableList<Period> periods, EventHandler<ActionEvent> buttonsActionEvent,
                                   boolean movable
    ) {
        periodsPane.getChildren().clear(); // vide le panneau avant de redessiner

        for (Period period : periods) {
            // Vérifie si la période appartient à la semaine affichée
            if (LocalDateUtils.getFirstDayOfWeek(period.getDate()).equals(firstDayOfWeek)) {
                // Crée le bouton représentant la période
                PeriodView periodButton = makePeriodButton(periodsPane, period, buttonsActionEvent, movable);

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

    // Crée et configure un bouton (PeriodView) représentant une période
    private PeriodView makePeriodButton(Pane periodsPane,
                                        Period period, EventHandler<ActionEvent> actionEvent, boolean movable
    ) {
        PeriodView periodButton = new PeriodView(period); // bouton personnalisé avec données de période

        periodButton.setText(period.getPeriodType().getTitle()); // définit le texte du bouton (titre du type de période)

        // Applique une couleur de fond personnalisée selon le type de période
        periodButton.getStyleClass().add("period-button");
        periodButton.setStyle("-fx-background-color: " + period.getPeriodType().getRGBColor() + ";");

        // Largeur : 90% de la largeur de cellule
        periodButton.prefWidthProperty().bind(periodsPane.widthProperty().divide(7).multiply(0.9));

        // Hauteur : proportion de la durée de la période par rapport à une journée (en secondes)
        periodButton.prefHeightProperty().bind(
                periodsPane.heightProperty().multiply((double) period.getDuration().getSeconds() / 86400)
        );

        // Rend le boutton mobile avec un "click and drag"
        if (movable) {
            periodButton.setOnMousePressed(mouseEvent -> {
                periodButton.layoutYProperty().unbind();
                periodButton.layoutXProperty().unbind();
                periodButton.offsetY = mouseEvent.getSceneY() - periodButton.getLayoutY();
                periodButton.offsetX = mouseEvent.getSceneX() - periodButton.getLayoutX();
            });

            periodButton.setOnMouseDragged(mouseEvent -> {
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
            });
        }

        // En lachant la souris, un event choisi est exécuté
        periodButton.setOnAction(actionEvent);

        return periodButton; // retourne le bouton prêt à être affiché
    }
}
