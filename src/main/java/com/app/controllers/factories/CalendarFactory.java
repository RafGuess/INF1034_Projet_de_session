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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
        timesPane.minWidthProperty().bind(Bindings.divide(calendarCellWidth,2));

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
            ((ButtonBase) button).prefWidthProperty().bind(Bindings.multiply(calendarCellWidth, 10.0/7));
            ((ButtonBase) button).prefHeightProperty().bind(calendarCellHeight);
        });

    }

    public void updateDayBar(Pane dayPane, LocalDate startDate) {
        dayPane.getChildren().clear();
        dayPane.prefWidthProperty().bind(Bindings.multiply(calendarCellWidth,7));
        dayPane.minHeightProperty().bind(Bindings.divide(calendarCellHeight,2));

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

        label.maxWidthProperty().bind(rectangle.widthProperty());
        label.maxHeightProperty().bind(rectangle.heightProperty());

        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setText(LocalDateUtils.formatForCalendar(date));

        return stackPane;
    }

    public void makeTimerButtons(HBox timerButtonsHBox,
                                 EventHandler<MouseEvent> startTimerEvent,
                                 EventHandler<MouseEvent> resetTimerEvent,
                                 EventHandler<MouseEvent> stopTimerEvent
    ) {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/com/app/images/timer.png")).toExternalForm());
        ImageView imageView = new ImageView(image);

        imageView.fitHeightProperty().bind(calendarCellHeight);
        imageView.setPreserveRatio(true);

        timerButtonsHBox.getChildren().addAll(
                makeFancyButton(2.5, false, null, null, "timer", "timer-label"),
                makeFancyButton(2.5, false, startTimerEvent, "Commencer", "timer-start", "timer-start-label"),
                imageView,
                makeFancyButton(1.25, true, resetTimerEvent, "Réinitialiser", "timer-reset", "timer-reset-label"),
                makeFancyButton(1.25, true, stopTimerEvent, "Arrêter", "timer-stop", "timer-stop-label")
        );

    }

    private StackPane makeFancyButton(double widthFactor, boolean mirror, EventHandler<MouseEvent> mouseEvent,
                                      String text, String buttonStyleClass, String labelStyleClass) {
        // Button properties
        ObservableNumberValue fancyButtonWidth = Bindings.multiply(calendarCellWidth, widthFactor);
        ObservableNumberValue fancyButtonHeight = calendarCellHeight;
        ObservableNumberValue radiusX = Bindings.multiply(calendarCellWidth,2);
        ObservableNumberValue radiusY = Bindings.multiply(calendarCellHeight,2);

        // Path definition
        MoveTo start = new MoveTo(0,0);

        LineTo topLine = new LineTo();
        topLine.xProperty().bind(fancyButtonWidth);

        ArcTo rightSideArc = new ArcTo();
        rightSideArc.xProperty().bind(fancyButtonWidth);
        rightSideArc.yProperty().bind(fancyButtonHeight);
        rightSideArc.radiusXProperty().bind(radiusX);
        rightSideArc.radiusYProperty().bind(radiusY);
        rightSideArc.setSweepFlag(mirror);

        LineTo bottomLine = new LineTo();
        bottomLine.yProperty().bind(fancyButtonHeight);

        ArcTo leftSideArc = new ArcTo();
        leftSideArc.radiusXProperty().bind(radiusX);
        leftSideArc.radiusYProperty().bind(radiusY);
        leftSideArc.setSweepFlag(!mirror);

        // Setting path
        Path path = new Path(start, topLine, rightSideArc, bottomLine, leftSideArc);
        path.setOnMouseClicked(mouseEvent);
        path.getStyleClass().add(buttonStyleClass);

        // Setting label
        Label label = new Label(text);
        label.maxWidthProperty().bind(fancyButtonWidth);
        label.maxHeightProperty().bind(fancyButtonHeight);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setMouseTransparent(true);
        label.getStyleClass().add(labelStyleClass);

        StackPane fancyButtonStackPane = new StackPane();
        fancyButtonStackPane.getChildren().addAll(path, label);

        return fancyButtonStackPane;
    }
}
