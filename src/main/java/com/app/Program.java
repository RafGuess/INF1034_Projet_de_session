package com.app;

import com.app.controllers.CalendarController;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Program extends Application {
    final private static double appWidth = 1100;
    final private static double appHeight = 800;
    private static Stage primaryStage;
    private static Scene calendarScene;
    private static Scene addPeriodScene;
    private static Scene taskScene;
    private static Scene statisticsScene;
    private static Scene settingsScene;
    private static Scene welcomeScene;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        stage.setTitle("ProductivityApp");

        buildCalendarScene();
        buildAddPeriodScene();
        buildTaskScene();
        buildStatisticsScene();
        buildSettingsScene();
        buildWelcomeScene();

        primaryStage.setScene(calendarScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

    }

    public static void buildCalendarScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Program.class.getResource("calendar-view.fxml"));
        calendarScene = new Scene(fxmlLoader.load(), appWidth, appHeight);
        CalendarController controller = fxmlLoader.getController();

        calendarScene.getStylesheets().add(
                Objects.requireNonNull(Program.class.getResource("Styles/calendar.css")).toExternalForm()
        );
        controller.drawScene();
    }

    public static void buildAddPeriodScene() throws IOException {
        //todo
    }

    public static void buildTaskScene() throws IOException {
        //todo
    }

    public static void buildStatisticsScene() throws IOException {
        //todo
    }

    public static void buildSettingsScene() throws IOException {
        //todo
    }

    public static void buildWelcomeScene() throws IOException {
        //todo
    }

    public static ReadOnlyDoubleProperty getWidthProperty() {
        return primaryStage.widthProperty();
    }

    public static ReadOnlyDoubleProperty getHeightProperty() {
        return primaryStage.heightProperty();
    }

}