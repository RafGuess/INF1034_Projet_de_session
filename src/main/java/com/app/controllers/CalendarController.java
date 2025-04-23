package com.app.controllers;

import com.app.AppManager;
import com.app.controllers.controllerInterfaces.Cleanable;
import com.app.controllers.factories.CalendarFactory;
import com.app.controllers.factories.PeriodFactory;
import com.app.controllers.customNodes.PeriodNode;
import com.app.models.Database;
import com.app.models.Period;
import com.app.timer.Timer;
import com.app.models.User;
import com.app.timer.TimerListener;
import com.app.timer.TimerView;
import com.app.utils.LocalDateUtils;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarController implements Cleanable {

    // Panneaux pour l'affichage du calendrier, des périodes, et de la ligne de temps actuelle
    @FXML private Pane calendarPane;
    @FXML private Pane periodsPane;
    @FXML private Pane currentTimePane;
    @FXML private Line currentTimeLine;

    // Étiquettes des heures (gauche du calendrier)
    @FXML private Pane timesPane;

    // Barre des jours en haut du calendrier
    @FXML private Pane dayPane;
    @FXML private Button previousWeekButton;
    @FXML private Button nextWeekButton;

    // Boutons de gestion des périodes
    @FXML private VBox periodButtonsVBox;
    @FXML private Button addPeriodButton;
    @FXML private ToggleButton movePeriodButton;
    @FXML private ToggleButton cancelPeriodButton;

    // Zone contenant les boutons du minuteur
    @FXML private HBox timerHBox;

    // Date du premier jour de la semaine affichée
    private LocalDate currentFirstDayOfWeek;

    // Diviseur de mise à l’échelle UI (utilisé pour layout réactif)
    final private static int scaleDivisor = 9;

    // Usines pour dessiner et gérer les composants du calendrier
    private final PeriodFactory periodFactory = new PeriodFactory();
    private final CalendarFactory calendarFactory = new CalendarFactory(
            AppManager.getWidthProperty().divide(scaleDivisor), AppManager.getHeightProperty().divide(scaleDivisor)
    );

    // Listener déclenché lorsqu'une période est ajoutée/supprimée
    private final ListChangeListener<Period> periodListChangeListener = change -> updateCalendar();
    private final TimerListener timerView = new TimerView(timerHBox);

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

        // Ajoute un listener pour le minuteur
        TimerView timerView = new TimerView(timerHBox);
        Timer.addListener(timerView);
        timerView.changedTimer(null, 0, 0); // initialise à 00:00:00

        // Ajoute un listener pour mettre à jour les périodes si modifiées
        Database.addListenerToPeriodsOfUser(Database.getConnectedUser(), periodListChangeListener);

        // Démarre le thread de mise à jour continue
        continuousUpdateThread.start();

        // Assigne à l'interface une liste de raccourcis clavier
        Platform.runLater(() -> {
            Scene scene = calendarPane.getScene();
            scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        });
    }

    // Nettoie les listeners et threads lors de la destruction du contrôleur
    @Override
    public void cleanup() {
        Database.removeListenerFromPeriodsOfUser(Database.getConnectedUser(), periodListChangeListener);
        Timer.removeListener(timerView);
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
        AppManager.showPopup("Ajouter Période", "add-period-view.fxml", null);
    }

    // Supprime la période cliquée
    public void onPeriodCanceled(MouseEvent mouseEvent) {
        Database.removePeriod(((PeriodNode) mouseEvent.getSource()).getPeriod());
    }

    // Affiche la période dans un popup (consultation)
    public void onPeriodAccessed(MouseEvent mouseEvent) {
        Period period = ((PeriodNode) mouseEvent.getSource()).getPeriod();
        AppManager.showPopup(
            period.getPeriodType().getTitle(), "show-period-view.fxml", period
        );
    }

    // Méthode qui gère le déplacement d'une période
    public void onPeriodMoved(MouseEvent mouseEvent) {
        PeriodNode periodNode = ((PeriodNode) mouseEvent.getSource());

        // Détermination du nouveau temps de début et de fin de la période en fonction de sa position en Y
        double newYStartPos = periodNode.getLayoutY();
        double newYEndPos = newYStartPos + periodNode.getHeight();
        double paneHeight = periodsPane.getHeight();
        LocalTime newStartTime = LocalTime.ofSecondOfDay((long)(newYStartPos/paneHeight * 86400));
        LocalTime newEndTime = LocalTime.ofSecondOfDay((long)(newYEndPos/paneHeight * 86400));

        // Détermination de la nouvelle date de la période en fonction de sa position en X
        LocalDate newDate = currentFirstDayOfWeek;
        double cellWidth = periodsPane.getWidth()/7;
        int i = 1;
        while (periodNode.getLayoutX() > cellWidth * i) {
            newDate = newDate.plusDays(1);
            i++;
        }

        // Vérification de la disponibilités des collaborateurs actuels
        Period period = periodNode.getPeriod();
        User unavailableUser = Database.updatePeriodTime(period, newDate, newStartTime, newEndTime);

        // Si la période nouvellement crée respecte les échéanciers de tous, MAJ de la BD, sinon action annulée
        if (unavailableUser != null) {
            AppManager.showAlert(Alert.AlertType.ERROR, "Collaborateur indisponible",
                    String.format("Le collaborateur %s est indisponible à ce moment.", unavailableUser)
            );
            updateCalendar();
        }

    }

    // Met à jour l’affichage du calendrier
    @FXML
    private void updateCalendar() {
        EventHandler<MouseEvent> buttonEvent = this::onPeriodAccessed;
        boolean movable = false;

        // Empêche les deux boutons d'action d’être activés en même temps
        if (cancelPeriodButton.isSelected() && movePeriodButton.isSelected()) {
            cancelPeriodButton.setSelected(false);
            movePeriodButton.setSelected(false);
        } else if (movePeriodButton.isSelected()) {
            buttonEvent = this::onPeriodMoved;
            movable = true;
        } else if (cancelPeriodButton.isSelected()) {
            buttonEvent = this::onPeriodCanceled;
        }

        // Affiche les périodes dans le calendrier
        periodFactory.updateShownPeriods(
                periodsPane, currentFirstDayOfWeek,
                Database.getPeriodsOfUser(Database.getConnectedUser()),
                buttonEvent, movable
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

    // Méthode qui active des fonctionnalités du calendrier avec les touches du clavier
    private void handleKeyPress(KeyEvent keyEvent) {
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
