package com.app.controllers.factories;

import com.app.utils.LocalDateUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarFactory {

    private final ObservableDoubleValue calendarCellWidth;
    private final ObservableDoubleValue calendarCellHeight;

    public CalendarFactory(ObservableDoubleValue calendarCellWidth, ObservableDoubleValue calendarCellHeight) {
        this.calendarCellWidth = calendarCellWidth;
        this.calendarCellHeight = calendarCellHeight;
    }

    public void drawCalendarGrid(Pane calendarPane) {
        calendarPane.minWidthProperty().bind(Bindings.multiply(calendarCellWidth,7));
        calendarPane.minHeightProperty().bind(Bindings.multiply(calendarCellHeight,6));
        calendarPane.maxWidthProperty().bind(Bindings.multiply(calendarCellWidth,7));
        calendarPane.maxHeightProperty().bind(Bindings.multiply(calendarCellHeight,6));

        List<Node> calendarNodes = calendarPane.getChildren();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                Rectangle rectangle = makeCalendarRectangle();
                rectangle.xProperty().bind(Bindings.multiply(rectangle.widthProperty(),j));
                rectangle.yProperty().bind(Bindings.multiply(rectangle.heightProperty(),i));

                calendarNodes.add(rectangle);
            }
        }
    }

    private Rectangle makeCalendarRectangle() {
        Rectangle rectangle = new Rectangle();
        rectangle.getStyleClass().add("calendarRectangle");
        rectangle.widthProperty().bind(calendarCellWidth);
        rectangle.heightProperty().bind(calendarCellHeight);
        return rectangle;
    }

    public void drawTimes(Pane timesPane) {
        timesPane.maxHeightProperty().bind(Bindings.multiply(calendarCellHeight,6));

        for (int i = 0; i < 12; i++) {
            LocalTime time = LocalTime.of(i*2,0);

            Label label = makeTimeLabel(time);
            label.layoutYProperty().bind(Bindings.divide(Bindings.multiply(calendarCellHeight,i),2));

            Line line = makeTimeLineConnection();
            line.startYProperty().bind(label.layoutYProperty());
            line.endYProperty().bind(label.layoutYProperty());

            timesPane.getChildren().addAll(line,label);
        }
    }

    private Label makeTimeLabel(LocalTime time) {
        Label label = new Label(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        label.getStyleClass().add("timeLabel");
        label.prefWidthProperty().bind(Bindings.divide(calendarCellWidth,3));
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private Line makeTimeLineConnection() {
        Line line = new Line();
        line.setStartX(0);
        line.endXProperty().bind(Bindings.divide(calendarCellWidth,2));
        return line;
    }

    public void resizePeriodButtons(VBox periodButtonsVBox) {
        periodButtonsVBox.maxHeightProperty().bind(Bindings.multiply(calendarCellHeight,6));
        periodButtonsVBox.spacingProperty().bind(Bindings.divide(calendarCellHeight,4));

        periodButtonsVBox.getChildren().forEach(button -> {
            ((Button) button).prefWidthProperty().bind(Bindings.multiply(calendarCellWidth, 10.0/7));
            ((Button) button).prefHeightProperty().bind(calendarCellHeight);
        });

    }

    public void updateDayBar(Pane dayPane, LocalDate startDate) {
        dayPane.getChildren().clear();
        dayPane.prefWidthProperty().bind(Bindings.multiply(calendarCellWidth,7));

        for (int i = 0; i < 7; i++) {
            StackPane stackPane = makeDateStack(startDate);
            stackPane.layoutXProperty().bind(Bindings.multiply(calendarCellWidth,i+1.0/6));

            dayPane.getChildren().add(stackPane);
            startDate = startDate.plusDays(1);
        }
    }

    private StackPane makeDateStack(LocalDate date) {
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle();
        Label label = new Label();
        stackPane.getChildren().addAll(rectangle, label);

        rectangle.getStyleClass().add("dayNodes");
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        rectangle.widthProperty().bind(Bindings.divide(calendarCellWidth,1.5));
        rectangle.heightProperty().bind(Bindings.divide(calendarCellHeight,2));

        label.minHeightProperty().bind(rectangle.heightProperty());
        label.maxHeightProperty().bind(rectangle.heightProperty());

        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setText(LocalDateUtils.formatForCalendar(date));

        return stackPane;
    }
}
