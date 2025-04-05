package com.app.controllers.factories;

import com.app.utils.LocalDateUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class CalendarFactory {

    // Largeur et hauteur d'une cellule du calendrier, liées dynamiquement (via binding)
    private final ObservableDoubleValue calendarCellWidth;
    private final ObservableDoubleValue calendarCellHeight;

    // Constructeur pour initialiser les dimensions des cellules
    public CalendarFactory(ObservableDoubleValue calendarCellWidth, ObservableDoubleValue calendarCellHeight) {
        this.calendarCellWidth = calendarCellWidth;
        this.calendarCellHeight = calendarCellHeight;
    }

    // Dessine la grille du calendrier (6 lignes x 7 colonnes)
    public void drawCalendarGrid(Pane calendarPane) {
        // Définit les dimensions du panneau en fonction des cellules
        calendarPane.minWidthProperty().bind(Bindings.multiply(calendarCellWidth,7));
        calendarPane.minHeightProperty().bind(Bindings.multiply(calendarCellHeight,6));
        calendarPane.maxWidthProperty().bind(Bindings.multiply(calendarCellWidth,7));
        calendarPane.maxHeightProperty().bind(Bindings.multiply(calendarCellHeight,6));

        List<Node> calendarNodes = calendarPane.getChildren();

        // Crée une grille de rectangles (6 semaines, 7 jours)
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                Rectangle rectangle = makeCalendarRectangle();

                // Positionne dynamiquement le rectangle selon sa ligne (i) et sa colonne (j)
                rectangle.xProperty().bind(Bindings.multiply(rectangle.widthProperty(), j));
                rectangle.yProperty().bind(Bindings.multiply(rectangle.heightProperty(), i));

                calendarNodes.add(rectangle);
            }
        }
    }

    // Crée un rectangle pour une cellule du calendrier avec style
    private Rectangle makeCalendarRectangle() {
        Rectangle rectangle = new Rectangle();
        rectangle.getStyleClass().add("calendarRectangle"); // style CSS
        rectangle.widthProperty().bind(calendarCellWidth);
        rectangle.heightProperty().bind(calendarCellHeight);
        return rectangle;
    }

    // Surligne la colonne correspondant au jour actuel si la semaine affichée est la semaine actuelle
    public void updateHighlightDay(Pane calendarPane, LocalDate firstDayOfWeek) {
        Color color = firstDayOfWeek.isEqual(LocalDateUtils.getFirstDayOfWeek(LocalDate.now())) ? Color.GREY : null;

        for (int i = 0; i < 6; i++) {
            getCalendarRectangle(calendarPane, i, LocalDate.now().getDayOfWeek().getValue() % 7).setFill(color);
        }
    }

    // Récupère un rectangle à une position précise dans la grille
    private Rectangle getCalendarRectangle(Pane calendarPane, int row, int col) {
        return (Rectangle) calendarPane.getChildren().get(7 * row + col);
    }

    // Dessine les heures sur le côté gauche du calendrier (12 lignes = 24h avec des tranches de 2h)
    public void drawTimes(Pane timesPane) {
        timesPane.maxHeightProperty().bind(Bindings.multiply(calendarCellHeight, 6));
        timesPane.minWidthProperty().bind(Bindings.divide(calendarCellWidth, 2));

        for (int i = 0; i < 12; i++) {
            LocalTime time = LocalTime.of(i * 2, 0); // chaque 2 heures

            Label label = makeTimeLabel(time);
            label.layoutYProperty().bind(Bindings.divide(Bindings.multiply(calendarCellHeight, i), 2));

            Line line = makeTimeLineConnection();
            line.startYProperty().bind(label.layoutYProperty());
            line.endYProperty().bind(label.layoutYProperty());

            timesPane.getChildren().addAll(line, label);
        }
    }

    // Crée un label avec l'heure formatée (HH:mm)
    private Label makeTimeLabel(LocalTime time) {
        Label label = new Label(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        label.getStyleClass().add("timeLabel");
        label.prefWidthProperty().bind(Bindings.divide(calendarCellWidth, 3));
        label.setAlignment(Pos.CENTER);
        return label;
    }

    // Crée une ligne horizontale pour relier le label horaire au calendrier
    private Line makeTimeLineConnection() {
        Line line = new Line();
        line.setStartX(0);
        line.endXProperty().bind(Bindings.divide(calendarCellWidth, 2));
        return line;
    }

    // Redimensionne dynamiquement les boutons de période (dans VBox à droite)
    public void resizePeriodButtons(VBox periodButtonsVBox) {
        periodButtonsVBox.maxHeightProperty().bind(Bindings.multiply(calendarCellHeight, 6));
        periodButtonsVBox.spacingProperty().bind(Bindings.divide(calendarCellHeight, 4));

        // Applique les propriétés à chaque bouton enfant
        periodButtonsVBox.getChildren().forEach(button -> {
            ((ButtonBase) button).prefWidthProperty().bind(Bindings.multiply(calendarCellWidth, 10.0 / 7));
            ((ButtonBase) button).prefHeightProperty().bind(calendarCellHeight);
        });
    }

    // Met à jour la barre du haut avec les jours (dates)
    public void updateDayBar(Pane dayPane, LocalDate startDate) {
        dayPane.getChildren().clear();
        dayPane.prefWidthProperty().bind(Bindings.multiply(calendarCellWidth, 7));
        dayPane.minHeightProperty().bind(Bindings.divide(calendarCellHeight, 2));

        for (int i = 0; i < 7; i++) {
            StackPane stackPane = makeDateStack(startDate);
            stackPane.layoutXProperty().bind(Bindings.multiply(calendarCellWidth, i + 1.0 / 6));
            dayPane.getChildren().add(stackPane);
            startDate = startDate.plusDays(1); // passe au jour suivant
        }
    }

    // Crée un StackPane avec une date affichée dans un rectangle
    private StackPane makeDateStack(LocalDate date) {
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle();
        Label label = new Label();
        stackPane.getChildren().addAll(rectangle, label);

        rectangle.getStyleClass().add("dayNodes");
        rectangle.setArcWidth(10); // coins arrondis
        rectangle.setArcHeight(10);
        rectangle.widthProperty().bind(Bindings.divide(calendarCellWidth, 1.5));
        rectangle.heightProperty().bind(Bindings.divide(calendarCellHeight, 2));

        label.maxWidthProperty().bind(rectangle.widthProperty());
        label.maxHeightProperty().bind(rectangle.heightProperty());
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setText(LocalDateUtils.formatForCalendar(date)); // format personnalisé

        return stackPane;
    }

    // Crée les boutons du minuteur (timer) dans une HBox
    public void makeTimerButtons(
            HBox timerButtonsHBox,
            EventHandler<MouseEvent> startTimerEvent,
            EventHandler<MouseEvent> resetTimerEvent,
            EventHandler<MouseEvent> stopTimerEvent
    ) {
        // Image décorative de sablier
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/com/app/images/timer.png")).toExternalForm());
        ImageView imageView = new ImageView(image);

        imageView.fitWidthProperty().bind(Bindings.multiply(calendarCellWidth, 0.25));
        imageView.setPreserveRatio(true);

        // Ajoute les boutons stylisés et l'image
        timerButtonsHBox.getChildren().addAll(
                makeFancyButton(2.5, false, null, null, "timer", "timer-label"),
                makeFancyButton(2.5, false, startTimerEvent, "Commencer", "timer-start", "timer-start-label"),
                imageView,
                makeFancyButton(1.25, true, resetTimerEvent, "Réinitialiser", "timer-reset", "timer-reset-label"),
                makeFancyButton(1.25, true, stopTimerEvent, "Arrêter", "timer-stop", "timer-stop-label")
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
        ObservableNumberValue fancyButtonWidth = Bindings.multiply(calendarCellWidth, widthFactor);
        ObservableNumberValue fancyButtonHeight = calendarCellHeight;
        ObservableNumberValue radiusX = Bindings.multiply(calendarCellWidth, 2);
        ObservableNumberValue radiusY = Bindings.multiply(calendarCellHeight, 2);

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

        return fancyButtonStackPane;
    }
}
