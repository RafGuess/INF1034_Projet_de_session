package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.controllerInterfaces.Cleanable;
import com.app.controllers.factories.CalendarFactory;
import com.app.controllers.factories.PeriodFactory;
import com.app.controllers.viewModels.PeriodView;
import com.app.models.Database;
import com.app.models.Period;
import com.app.models.Timer;
import com.app.utils.LocalDateUtils;
import com.app.utils.ThemeManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CalendarController implements Cleanable {

    // Panneaux pour l'affichage du calendrier, des périodes, et de la ligne de temps actuelle
    @FXML
    private Pane calendarPane;
    @FXML
    private Pane periodsPane;
    @FXML
    private Pane currentTimePane;
    @FXML
    private Line currentTimeLine;

    // Étiquettes des heures (gauche du calendrier)
    @FXML
    private Pane timesPane;

    // Barre des jours en haut du calendrier
    @FXML
    private Pane dayPane;
    @FXML
    private Button previousWeekButton;
    @FXML
    private Button nextWeekButton;

    // Boutons de gestion des périodes
    @FXML
    private VBox periodButtonsVBox;
    @FXML
    private Button addPeriodButton;
    @FXML
    private ToggleButton movePeriodButton;
    @FXML
    private ToggleButton cancelPeriodButton;

    // Zone contenant les boutons du minuteur
    @FXML
    private HBox timerHBox;

    // Date du premier jour de la semaine affichée
    private LocalDate currentFirstDayOfWeek;

    // Diviseur de mise à l’échelle UI (utilisé pour layout réactif)
    final private static int scaleDivisor = 9;

    // Usines pour dessiner et gérer les composants du calendrier
    private final CalendarFactory calendarFactory = new CalendarFactory(
            AppManager.getWidthProperty().divide(scaleDivisor), AppManager.getHeightProperty().divide(scaleDivisor)
    );

    private final PeriodFactory periodFactory = new PeriodFactory(
            AppManager.getWidthProperty().divide(scaleDivisor)
    );

    // Listener déclenché lorsqu'une période est ajoutée/supprimée
    private ListChangeListener<Period> periodListChangeListener;

    // Thread qui met à jour la ligne de l'heure actuelle en temps réel
    private final AnimationTimer continuousUpdateThread = new AnimationTimer() {
        @Override
        public void handle(long l) {
            // Calcule la fraction du jour actuelle (en secondes)
            double fractionOfDay = (double) LocalTime.now().toSecondOfDay() / 86400;

            // Met à jour la position Y de la ligne d’heure actuelle
            currentTimeLine.startYProperty().bind(Bindings.multiply(currentTimePane.heightProperty(), fractionOfDay));
            currentTimeLine.endYProperty().bind(Bindings.multiply(currentTimePane.heightProperty(), fractionOfDay));
            currentTimeLine.endXProperty().bind(currentTimePane.widthProperty()); // ligne horizontale complète
        }
    };

    // Méthode appelée à l'initialisation du contrôleur (après chargement du FXML)
    public void initialize() {
        currentFirstDayOfWeek = LocalDateUtils.getFirstDayOfWeek(LocalDate.now()); // semaine en cours

        // Dessine les composants du calendrier (grille, heures, boutons)
        calendarFactory.drawCalendarGrid(calendarPane);
        calendarFactory.drawTimes(timesPane);
        calendarFactory.resizePeriodButtons(periodButtonsVBox);
        calendarFactory.makeTimerButtons(timerHBox, this::onStartTimer, this::onResetTimer, this::onStopTimer);

        // Met à jour le calendrier une fois l'interface prête
        Platform.runLater(this::updateCalendar);

        // Redimensionne les boutons précédent/suivant selon la largeur des heures
        previousWeekButton.prefWidthProperty().bind(timesPane.widthProperty());
        nextWeekButton.prefWidthProperty().bind(timesPane.widthProperty());

        // Lie les dimensions des panneaux au calendrier
        periodsPane.prefWidthProperty().bind(calendarPane.widthProperty());
        periodsPane.prefHeightProperty().bind(calendarPane.heightProperty());
        currentTimePane.prefWidthProperty().bind(calendarPane.widthProperty());
        currentTimePane.prefHeightProperty().bind(calendarPane.heightProperty());

        // Permet les clics sur les boutons en dessous de la ligne d'heure actuelle
        currentTimeLine.setMouseTransparent(true);

        // Ajoute un listener pour mettre à jour les périodes si modifiées
        periodListChangeListener = change -> updateCalendar();
        Database.addListenerToPeriodsOfUser(Database.getConnectedUser(), periodListChangeListener);

        // Ajoute un listener pour le minuteur
        Timer.addListener(this::updateTimer);
        updateTimer(null, 0, 0); // initialise à 00:00:00

        // Démarre le thread de mise à jour continue
        continuousUpdateThread.start();

        Platform.runLater(() -> {
            Scene scene = calendarPane.getScene();
            scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        });

        // Ajout du listener par Samir pour forcer la màj lors du passage au dark mode
        ThemeManager.getInstance().darkModeProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::updateCalendar);
        });
    }

    // Nettoie les listeners et threads lors de la destruction du contrôleur
    @Override
    public void cleanup() {
        Database.removeListenerFromPeriodsOfUser(Database.getConnectedUser(), periodListChangeListener);
        Timer.removeListener(this::updateTimer);
        continuousUpdateThread.stop();
    }

    // Passe à la semaine suivante
    @FXML
    public void onNextWeekButtonClicked() {
        currentFirstDayOfWeek = currentFirstDayOfWeek.plusWeeks(1);
        updateCalendar();
    }

    // Revient à la semaine précédente
    @FXML
    public void onPreviousWeekButtonClicked() {
        currentFirstDayOfWeek = currentFirstDayOfWeek.minusWeeks(1);
        updateCalendar();
    }

    // Affiche la fenêtre d’ajout d’une nouvelle période
    @FXML
    public void onCreatePeriodButtonClicked() {
        AppManager.showPopup("add-period-view.fxml", null);
    }

    // Bouton pour déplacer une période (non implémenté encore)
    @FXML
    public void onMovePeriodButtonClicked() {
        // todo
    }

    // Supprime la période cliquée
    public void onPeriodCanceled(ActionEvent actionEvent) {
        Database.removePeriod(((PeriodView) actionEvent.getSource()).getPeriod());
    }

    // Affiche la période dans un popup (consultation)
    public void onPeriodAccessed(ActionEvent actionEvent) {
        AppManager.showPopup(
                "show-period-view.fxml", ((PeriodView) actionEvent.getSource()).getPeriod()
        );
    }

    // Fonction pour déplacer une période (non utilisée pour le moment)
    public void onPeriodMoved(ActionEvent actionEvent) {
        // todo, maybe unnecessary
    }

    // Met à jour l’affichage du calendrier
    @FXML
    private void updateCalendar() {
        EventHandler<ActionEvent> actionEvent = this::onPeriodAccessed;

        // Empêche les deux boutons d'action d’être activés en même temps
        if (cancelPeriodButton.isSelected() && movePeriodButton.isSelected()) {
            cancelPeriodButton.setSelected(false);
            movePeriodButton.setSelected(false);
        } else if (movePeriodButton.isSelected()) {
            // todo
        } else if (cancelPeriodButton.isSelected()) {
            actionEvent = this::onPeriodCanceled;
        }

        // Affiche les périodes dans le calendrier
        periodFactory.updateShownPeriods(
                periodsPane, currentFirstDayOfWeek,
                Database.getPeriodsOfUser(Database.getConnectedUser()),
                actionEvent
        );

        // Met à jour la barre des jours et la surbrillance du jour actuel
        calendarFactory.updateDayBar(dayPane, currentFirstDayOfWeek);
        calendarFactory.updateHighlightDay(calendarPane, currentFirstDayOfWeek);
    }

    // Démarre le minuteur
    private void onStartTimer(MouseEvent mouseEvent) {
        Timer.startTimer();
    }

    // Réinitialise le minuteur
    private void onResetTimer(MouseEvent mouseEvent) {
        Timer.resetTimer();
    }

    // Arrête le minuteur
    private void onStopTimer(MouseEvent mouseEvent) {
        Timer.stopTimer();
    }

    // Met à jour l’affichage de l’heure du minuteur
    private void updateTimer(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Met à jour le label avec l'heure formatée (sur le premier bouton dans la HBox)
        Platform.runLater(() -> ((Label) ((StackPane) timerHBox.getChildren().getFirst()).getChildren().getLast())
                .setText(formatter.format(LocalTime.ofSecondOfDay(newValue.intValue()))));
    }

    // Méthode vide pour impression (potentiellement à implémenter)
    @FXML
    public void print() {

    }

    public void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case LEFT -> onPreviousWeekButtonClicked();
            case RIGHT -> onNextWeekButtonClicked();
            case DELETE -> {
                cancelPeriodButton.setSelected(!cancelPeriodButton.isSelected());
                updateCalendar();
            }
            case INSERT -> onCreatePeriodButtonClicked();
        }
        keyEvent.consume();
    }

}
