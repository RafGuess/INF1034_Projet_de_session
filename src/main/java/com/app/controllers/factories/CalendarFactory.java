package com.app.controllers.factories;

import com.app.utils.LocalDateUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableNumberValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendarFactory {

    // Largeur et hauteur d'une cellule du calendrier, liées dynamiquement (via binding)
    private final ObservableNumberValue calendarCellWidth;
    private final ObservableNumberValue calendarCellHeight;

    // Constructeur pour initialiser les dimensions des cellules
    public CalendarFactory(ObservableNumberValue calendarCellWidth, ObservableNumberValue calendarCellHeight) {
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
        rectangle.getStyleClass().add("calendar-rectangle"); // style CSS
        rectangle.widthProperty().bind(calendarCellWidth);
        rectangle.heightProperty().bind(calendarCellHeight);
        return rectangle;
    }

    // Surligne la colonne correspondant au jour actuel si la semaine affichée est la semaine actuelle
    public void updateHighlightDay(Pane calendarPane, LocalDate firstDayOfWeek) {
        boolean highlightDay = firstDayOfWeek.isEqual(LocalDateUtils.getFirstDayOfWeek(LocalDate.now()));

        for (int i = 0; i < 6; i++) {
            Rectangle rectangle = getCalendarRectangle(calendarPane, i, LocalDate.now().getDayOfWeek().getValue() % 7);
            if (highlightDay) rectangle.getStyleClass().add("calendar-rectangle-highlight");
            else rectangle.getStyleClass().remove("calendar-rectangle-highlight");
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
        label.getStyleClass().add("time-label");
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

        rectangle.getStyleClass().add("day-nodes");
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

    public void updateMonthLabel(Label monthLabel, LocalDate currentFirstDayOfWeek) {
        StringBuilder stringBuilder = new StringBuilder();
        Month currentMonth = currentFirstDayOfWeek.getMonth();
        Month endOfWeekMonth = currentFirstDayOfWeek.plusDays(7).getMonth();

        stringBuilder.append("CALENDRIER - ");
        stringBuilder.append(currentMonth.getDisplayName(TextStyle.FULL, Locale.FRENCH).toUpperCase());

        if (!currentMonth.equals(endOfWeekMonth)) {
            stringBuilder.append(" ➞ ");
            stringBuilder.append(endOfWeekMonth.getDisplayName(TextStyle.FULL, Locale.FRENCH).toUpperCase());
        }
        monthLabel.setText(stringBuilder.toString());
    }
}
