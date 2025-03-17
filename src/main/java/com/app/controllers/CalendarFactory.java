package com.app.controllers;

import com.app.models.Date;
import com.app.Program;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarFactory {
    final private Pane calendarPane;
    final private Pane timesPane;
    final private Pane dayPane;
    final private Button previousWeekButton;
    final private Button nextWeekButton;
    final private VBox periodButtonsVBox;

    public CalendarFactory(
            Pane calendarPane, Pane timesPane, Pane dayPane,
            Button previousWeekButton, Button nextWeekButton, VBox periodButtonsVBox) {
        this.calendarPane = calendarPane;
        this.timesPane = timesPane;
        this.dayPane = dayPane;
        this.previousWeekButton = previousWeekButton;
        this.nextWeekButton = nextWeekButton;
        this.periodButtonsVBox = periodButtonsVBox;
    }

    public void drawCalendarGrid() {
        List<Node> calendarNodes = calendarPane.getChildren();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.WHITE);
                rectangle.setStroke(Color.BLACK);

                rectangle.widthProperty().bind(Bindings.divide(Program.getWidthProperty(),10));
                rectangle.heightProperty().bind(Bindings.divide(Program.getHeightProperty(),10));

                rectangle.xProperty().bind(Bindings.multiply(rectangle.widthProperty(),j));
                rectangle.yProperty().bind(Bindings.multiply(rectangle.heightProperty(),i));

                calendarNodes.add(rectangle);
            }
        }
        calendarPane.maxHeightProperty().bind(Bindings.multiply(getRectangleHeightProperty(),6));
        calendarPane.maxWidthProperty().bind(Bindings.multiply(getRectangleWidthProperty(),7));
    }

    public void drawTimes() {
        List<Node> timesNodes = timesPane.getChildren();
        DoubleProperty rectangleHeight = ((Rectangle)calendarPane.getChildren().getFirst()).heightProperty();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < 12; i++) {
            LocalTime time = LocalTime.of(i*2,0);

            Label label = new Label(time.format(formatter));
            label.getStyleClass().add("timesNodes");
            label.layoutYProperty().bind(Bindings.divide(Bindings.multiply(rectangleHeight,i),2));
            label.prefWidthProperty().bind(Bindings.divide(getRectangleWidthProperty(),3));
            label.setAlignment(Pos.CENTER);

            Line line = new Line();
            line.setStartX(0);
            line.endXProperty().bind(Bindings.multiply(label.prefWidthProperty(),1.5));
            line.startYProperty().bind(Bindings.divide(Bindings.multiply(rectangleHeight,i),2));
            line.endYProperty().bind(Bindings.divide(Bindings.multiply(rectangleHeight,i),2));

            timesNodes.add(label);
            timesNodes.add(line);
        }
        timesPane.maxHeightProperty().bind(Bindings.multiply(getRectangleHeightProperty(),6));
    }

    public void resizeButtons() {
        List<Node> buttonNodes = periodButtonsVBox.getChildren();
        buttonNodes.forEach(button -> {
            ((Button) button).prefWidthProperty().bind(Bindings.divide(Program.getWidthProperty(),7));
            ((Button) button).prefHeightProperty().bind(Bindings.divide(Program.getHeightProperty(),10));
        });

        periodButtonsVBox.spacingProperty().bind(Bindings.divide(Program.getHeightProperty(),40));
    }

    public void drawDayBar() {
        previousWeekButton.prefWidthProperty().bind(timesPane.widthProperty());
        nextWeekButton.prefWidthProperty().bind(timesPane.widthProperty());

        dayPane.prefWidthProperty().bind(calendarPane.widthProperty());
        Date date = Date.getFirstDayOfWeek();

        for (int i = 0; i < 7; i++) {
            StackPane stackPane = dateStackFactory(date, i);
            dayPane.getChildren().add(stackPane);
            date.nextDay();
        }
    }

    private StackPane dateStackFactory(Date date, int pos) {
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle();
        Label label = new Label("test");

        stackPane.getChildren().addAll(rectangle, label);

        rectangle.getStyleClass().add("dayNodes");
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        rectangle.widthProperty().bind(Bindings.divide(getRectangleWidthProperty(),1.5));
        rectangle.heightProperty().bind(Bindings.divide(getRectangleHeightProperty(),3));
        stackPane.layoutXProperty().bind(Bindings.multiply(getRectangleWidthProperty(),pos+1.0/6));

        label.setTextAlignment(TextAlignment.CENTER);
        label.setText(date.toString());

        return stackPane;
    }

    private DoubleProperty getRectangleWidthProperty() {
        return ((Rectangle)calendarPane.getChildren().getFirst()).widthProperty();
    }

    private DoubleProperty getRectangleHeightProperty() {
        return ((Rectangle)calendarPane.getChildren().getFirst()).heightProperty();
    }
}
