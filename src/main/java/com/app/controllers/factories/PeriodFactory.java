package com.app.controllers.factories;

import com.app.controllers.customNodes.PeriodNode;
import com.app.models.Period;
import com.app.utils.LocalDateUtils;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class PeriodFactory {

    private final PauseTransition delay = new PauseTransition(Duration.millis(250));

    // Met à jour l'affichage des périodes visibles dans une semaine donnée
    public void updateShownPeriods(Pane periodsPane, LocalDate firstDayOfWeek, ObservableList<Period> periods,
                                   EventHandler<MouseEvent> selectedEvent, EventHandler<MouseEvent> movedEvent, boolean cancellable
    ) {
        clearShownPeriods(periodsPane);

        // Initialisation du nouvel affichage de la semaine
        for (Period period : periods) {
            // Vérifie si la période appartient à la semaine affichée
            if (LocalDateUtils.getFirstDayOfWeek(period.getDate()).equals(firstDayOfWeek)) {
                // Crée le bouton représentant la période
                PeriodNode periodNode;
                if (cancellable) periodNode = makeCancellablePeriodButton(periodsPane, period, selectedEvent);
                else periodNode = makeMovablePeriodButton(periodsPane, period, selectedEvent, movedEvent);

                // Calcule le jour (colonne) où positionner le bouton (0 à 6)
                long i = ChronoUnit.DAYS.between(firstDayOfWeek, period.getDate());

                // Position horizontale (X) : basée sur la colonne
                periodNode.layoutXProperty().bind(
                        periodsPane.widthProperty().divide(7).multiply((double) i + 0.05)
                );

                // Position verticale (Y) : proportion de la journée écoulée
                periodNode.layoutYProperty().bind(
                        periodsPane.heightProperty().multiply( (double) period.getStartTime().toSecondOfDay() / 86400)
                );

                Label periodTimeLabel = periodNode.getAssociatedLabel();
                periodsPane.getChildren().add(periodNode); // ajoute le bouton à la vue
                periodsPane.getChildren().add(periodTimeLabel);

                // Ajout des labels de temps pour les périodes
                Platform.runLater( () -> {
                    updatePeriodTimeLabel(periodNode, periodTimeLabel, periodsPane.getHeight());
                    periodNode.layoutYProperty().addListener((change) -> updatePeriodTimeLabel(periodNode, periodTimeLabel, periodsPane.getHeight()));
                });
            }
        }
        // Met les labels au premier plan
        periodsPane.getChildren().stream()
                .filter(node -> node instanceof Label)
                .forEach(node -> Platform.runLater(node::toFront));
    }

    private void clearShownPeriods(Pane periodsPane) {
        // Vide toutes les périodes présentement affichée à l'écran, à l'exception des périodes
        // présentement déplacées par l'utilisateur.

        // Vu que le label de la période et le bouton de période sont traités comme deux noeuds différent, alors il faut
        // alors remettre le label de la période en mouvement afin de supporter le transfert des périodes
        // d'une semaine à l'autre.
        periodsPane.getChildren().removeIf(node -> !(node instanceof PeriodNode && ((PeriodNode)node).beingMoved));
        if (!periodsPane.getChildren().isEmpty()) {
            PeriodNode movedPeriod = (PeriodNode) periodsPane.getChildren().getFirst();
            periodsPane.getChildren().add(movedPeriod.getAssociatedLabel());
        }
    }

    private PeriodNode makeMovablePeriodButton(Pane periodsPane, Period period,
                                               EventHandler<MouseEvent> movedEvent, EventHandler<MouseEvent> selectedEvent
    ) {
        PeriodNode periodNode = makePeriodButton(periodsPane, period);
        // Rend le boutton mobile avec un "click and drag"
        periodNode.setOnMousePressed(mouseEvent -> movablePeriodClicked(
                movedEvent, selectedEvent, mouseEvent, periodNode, periodsPane
        ));
        return periodNode;
    }

    private PeriodNode makeCancellablePeriodButton(Pane periodsPane, Period period, EventHandler<MouseEvent> cancelEvent) {
        PeriodNode periodNode = makePeriodButton(periodsPane, period);
        periodNode.getStyleClass().add("cancellable");
        // Rend le boutton mobile avec un "click and drag"
        periodNode.setOnMouseReleased(cancelEvent);
        return periodNode;
    }

    // Crée et configure un bouton (PeriodView) représentant une période
    private PeriodNode makePeriodButton(Pane periodsPane, Period period) {
        PeriodNode periodNode = new PeriodNode(period); // bouton personnalisé avec données de période

        periodNode.setText(period.getPeriodType().getTitle()); // définit le texte du bouton (titre du type de période)

        // Applique une couleur de fond personnalisée selon le type de période
        periodNode.getStyleClass().add("period-button");
        periodNode.setStyle("-fx-background-color: " + period.getPeriodType().getRGBColor() + ";");
        periodNode.setStyle(periodNode.getStyle() + "-fx-text-fill: " + determineTextColor(period.getPeriodType().getColor()) + ";");

        // Largeur : 90% de la largeur de cellule
        ObservableDoubleValue width = periodsPane.widthProperty().divide(7).multiply(0.9);
        periodNode.minWidthProperty().bind(width);
        periodNode.maxWidthProperty().bind(width);

        // Hauteur : proportion de la durée de la période par rapport à une journée (en secondes)
        ObservableDoubleValue height = periodsPane.heightProperty().multiply((double) period.getDuration().getSeconds() / 86400);
        periodNode.minHeightProperty().bind(height);
        periodNode.maxHeightProperty().bind(height);

        return periodNode; // retourne le bouton prêt à être affiché
    }

    private String determineTextColor(Color color) {
        // Formule pour approximer la couleur à afficher (internet)
        double luminance = 0.2126*color.getRed() + 0.7152*color.getGreen() + 0.0722* color.getBlue();

        return luminance > 0.5 ? "black" : "white";
    }

    private void movablePeriodClicked(EventHandler<MouseEvent> selectedEvent, EventHandler<MouseEvent> movedEvent,
                               MouseEvent mouseEvent, PeriodNode periodNode, Pane periodsPane
    ) {
        periodNode.offsetY = mouseEvent.getSceneY() - periodNode.getLayoutY();
        periodNode.offsetX = mouseEvent.getSceneX() - periodNode.getLayoutX();

        if (mouseEvent.getClickCount() == 2) {
            delay.stop();
            periodNode.setOnMouseReleased(selectedEvent);
        } else if (mouseEvent.getClickCount() == 1) {
            delay.setOnFinished(e -> {
                periodNode.setOnMouseReleased(movedEvent);
                periodNode.layoutYProperty().unbind();
                periodNode.layoutXProperty().unbind();
                periodNode.setOnMouseDragged(dragEvent -> movablePeriodDragged(periodNode, dragEvent, periodsPane));
            });
            delay.playFromStart();

        }
    }

    private void movablePeriodDragged(PeriodNode periodNode, MouseEvent mouseEvent, Pane periodsPane) {
        periodNode.beingMoved = true;

        // Calcule la nouvelle position Y de la période et son un nouveau temps
        double newPosY = 0;
        double minDiff = Double.MAX_VALUE;
        for (int i = 0; i < 96; i++) {
            double pos = periodsPane.getHeight()* i / 96;
            double diff = Math.abs(pos - mouseEvent.getSceneY() + periodNode.offsetY);
            if (diff < minDiff && pos + periodNode.getHeight() <= periodsPane.getHeight()) {
                newPosY = pos;
                minDiff = diff;
            }
        }
        periodNode.setLayoutY(newPosY);

        // Calcule la nouvelle position X de la période et sa nouvelle date
        double newPosX = 0;
        minDiff = Double.MAX_VALUE;
        for (int i = 0; i < 7; i++) {
            double pos = periodsPane.getWidth()/7 * (i + 0.05);
            double diff = Math.abs(pos - mouseEvent.getSceneX() + periodNode.offsetX);
            if (diff < minDiff) {
                newPosX = pos;
                minDiff = diff;
            }
        }
        periodNode.setLayoutX(newPosX);
    }

    private void updatePeriodTimeLabel(PeriodNode periodNode, Label periodTimeLabel, double paneHeight) {
        Pair<LocalTime, LocalTime> times = calculatePeriodStartEndTime(periodNode, paneHeight);
        periodTimeLabel.setText(String.format("%s-%s", times.getKey(), times.getValue()));
    }

    public Pair<LocalTime, LocalTime> calculatePeriodStartEndTime(PeriodNode periodNode, double paneHeight) {
        // Détermination du nouveau temps de début et de fin de la période en fonction de sa position en Y
        double newYStartPos = periodNode.layoutYProperty().get();
        LocalTime newStartTime = LocalTime.ofSecondOfDay((long) (newYStartPos / paneHeight * 86399));
        LocalTime newEndTime = newStartTime.plus(periodNode.getPeriod().getDuration());

        // Arondissement de l'heure en minutes (car sinon il y a des imprécisions de float)
        if (newStartTime.getSecond() >= 30) newStartTime = newStartTime.plusMinutes(1);
        newStartTime = newStartTime.withSecond(0).withNano(0);
        if (newEndTime.getSecond() >= 30) newEndTime = newEndTime.plusMinutes(1);
        newEndTime = newEndTime.withSecond(0).withNano(0);
        if (newEndTime.equals(LocalTime.MIDNIGHT)) newEndTime = LocalTime.ofSecondOfDay(86399);

        return new Pair<>(newStartTime, newEndTime);
    }

    public LocalDate calculatePeriodDate(PeriodNode periodNode, LocalDate currentFirstDayOfWeek) {
        // Détermination de la nouvelle date de la période en fonction de sa position en X
        LocalDate newDate = currentFirstDayOfWeek;
        Pane periodsPane = (Pane)periodNode.getParent();
        double cellWidth = periodsPane.getWidth() / 7;
        int i = 1;
        while (periodNode.getLayoutX() > cellWidth * i) {
            newDate = newDate.plusDays(1);
            i++;
        }
        return newDate;
    }
}
