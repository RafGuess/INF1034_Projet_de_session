package com.app.controllers.factories;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableNumberValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

public class TimerFactory {
    // Largeur et hauteur d'une cellule du calendrier, liées dynamiquement (via binding)
    private final ObservableNumberValue buttonWidth;
    private final ObservableNumberValue buttonHeight;

    public TimerFactory(ObservableNumberValue buttonWidth, ObservableNumberValue buttonHeight) {
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
    }

    // Crée les boutons du minuteur (timer) dans une HBox
    public void makeTimerButtons(
            HBox timerButtonsHBox,
            EventHandler<MouseEvent> startTimerEvent,
            EventHandler<MouseEvent> resetTimerEvent,
            EventHandler<MouseEvent> stopTimerEvent
    ) {
        // Image décorative de chronomètre
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/com/app/images/timer.png")).toExternalForm());
        ImageView imageView = new ImageView(image);

        imageView.fitWidthProperty().bind(Bindings.multiply(buttonWidth, 0.25));
        imageView.setPreserveRatio(true);

        // Ajoute les boutons stylisés et l'image
        timerButtonsHBox.getChildren().addAll(
                makeFancyButton(2, false, null, null, "timer", "timer-label"),
                makeFancyButton(2, false, startTimerEvent, "Commencer", "timer-start", "timer-start-label"),
                imageView,
                makeFancyButton(1, true, resetTimerEvent, "Réinitialiser", "timer-reset", "timer-reset-label"),
                makeFancyButton(1, true, stopTimerEvent, "Arrêter", "timer-stop", "timer-stop-label")
        );
    }

    // Crée un bouton stylisé à partir d'un Path et d'un Label, avec des coins arrondis animés
    private StackPane makeFancyButton(
            double widthFactor,
            boolean mirror,
            EventHandler<MouseEvent> mouseEvent,
            String text,
            String buttonStyleClass,
            String labelStyleClass
    ) {
        // Définition des tailles dynamiques
        ObservableNumberValue fancyButtonWidth = Bindings.multiply(buttonWidth, widthFactor);
        ObservableNumberValue fancyButtonHeight = buttonHeight;
        ObservableNumberValue radiusX = buttonWidth;
        ObservableNumberValue radiusY = Bindings.multiply(buttonHeight,2);

        // Définition du contour du bouton (forme personnalisée avec Path)
        MoveTo start = new MoveTo(0, 0);

        LineTo topLine = new LineTo();
        topLine.xProperty().bind(fancyButtonWidth);

        ArcTo rightSideArc = new ArcTo();
        rightSideArc.xProperty().bind(fancyButtonWidth);
        rightSideArc.yProperty().bind(fancyButtonHeight);
        rightSideArc.radiusXProperty().bind(radiusX);
        rightSideArc.radiusYProperty().bind(radiusY);
        rightSideArc.setSweepFlag(mirror); // sens de l'arc

        LineTo bottomLine = new LineTo();
        bottomLine.yProperty().bind(fancyButtonHeight);

        ArcTo leftSideArc = new ArcTo();
        leftSideArc.radiusXProperty().bind(radiusX);
        leftSideArc.radiusYProperty().bind(radiusY);
        leftSideArc.setSweepFlag(!mirror); // arc opposé

        // Construction du Path du bouton
        Path path = new Path(start, topLine, rightSideArc, bottomLine, leftSideArc);
        path.setOnMouseClicked(mouseEvent); // gestionnaire d'événement
        path.getStyleClass().add(buttonStyleClass);

        // Création du label par-dessus le bouton
        Label label = new Label(text);
        label.maxWidthProperty().bind(fancyButtonWidth);
        label.maxHeightProperty().bind(fancyButtonHeight);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setMouseTransparent(true); // le clic passe au Path
        label.getStyleClass().add(labelStyleClass);

        // Empile le Path et le Label dans un StackPane
        StackPane fancyButtonStackPane = new StackPane();
        fancyButtonStackPane.getChildren().addAll(path, label);
        fancyButtonStackPane.setMinSize(0,0);
        label.setMinSize(0,0);

        return fancyButtonStackPane;
    }
}
