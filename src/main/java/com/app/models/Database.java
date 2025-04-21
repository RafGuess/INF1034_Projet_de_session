package com.app.models;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

    // Utilisateur actuellement connecté
    private static User connectedUser;

    // Liste observable de tous les utilisateurs
    final private static ObservableList<User> users = FXCollections.observableArrayList();

    // HashMap associant chaque utilisateur à une paire (liste de périodes, liste de types de période)
    final private static HashMap<User, Pair<ObservableList<Period>, ObservableList<PeriodType>>> userPeriodsHashMap = new HashMap<>();

    // Ajoute un listener pour le minuteur qui met à jour les objectifs de période
    static {
        Timer.addListener(Database::updateObjectives);
    }

    // Données de test injectées statiquement
    static {
        // Création d'utilisateurs
        addNewUser("admin", "1234", "Bob", "Bricoleur");
        addNewUser("superSlayer3000", "superPassword", "Alexandre", "Le Grand");
        addNewUser("whatDisAppAbout", "no", "Tony", "Stark");

        // Connexion à l'utilisateur admin
        connectedUser = getUser("admin");

        // Ajoute des types de période à l'utilisateur connecté
        addPeriodTypeToUser("Étude", Color.CYAN, Duration.ofHours(3), getConnectedUser());
        addPeriodTypeToUser("Activité physique", Color.LIGHTGREEN, Duration.ofHours(2), getConnectedUser());

        // Crée une liste de collaborateurs contenant l'utilisateur connecté
        ArrayList<User> users = new ArrayList<>();
        users.add(connectedUser);

        // Ajoute deux périodes de test
        addPeriod(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(4),
                getPeriodTypesOfUser(getConnectedUser()).get(1), "Ne pas oublier coffre à crayon", users);
        addPeriod(LocalDate.now(), LocalTime.now().minusHours(6), LocalTime.now().minusHours(4),
                getPeriodTypesOfUser(getConnectedUser()).get(2), "Ne pas oublier d'apporter des balles", users);
        addPeriod(LocalDate.now().minusDays(2), LocalTime.now().minusHours(3), LocalTime.now().minusHours(1),
                getPeriodTypesOfUser(getConnectedUser()).get(1), "Le local est le S-110", users);
        addPeriod(LocalDate.now().plusDays(1), LocalTime.now().minusHours(4), LocalTime.now().minusHours(2),
                getPeriodTypesOfUser(getConnectedUser()).get(2), "On reprend notre partie de 1-0", users);
    }

    // Ajoute un listener à la liste des périodes d’un utilisateur
    public static boolean addListenerToPeriodsOfUser(User user, ListChangeListener<Period> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getKey().addListener(listener);
            return true;
        }
        return false;
    }

    // Supprime un listener des périodes
    public static boolean removeListenerFromPeriodsOfUser(User user, ListChangeListener<Period> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getKey().removeListener(listener);
            return true;
        }
        return false;
    }

    // Ajoute un listener à la liste des types de période d’un utilisateur
    public static boolean addListenerToPeriodTypesOfUser(User user, ListChangeListener<PeriodType> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getValue().addListener(listener);
            return true;
        }
        return false;
    }

    // Supprime un listener des types de période
    public static boolean removeListenerToPeriodTypesOfUser(User user, ListChangeListener<PeriodType> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getValue().removeListener(listener);
            return true;
        }
        return false;
    }

    // Tente d'ajouter une nouvelle période pour un ou plusieurs utilisateurs
    public static User addPeriod(LocalDate date, LocalTime startTime, LocalTime endTime, PeriodType periodType, String notes, List<User> collaborators) {
        Period period = new Period(date, startTime, endTime, periodType, notes, collaborators);

        User unavailableUser = checkAvailability(date, startTime, endTime, collaborators);
        if (unavailableUser != null) {
            return unavailableUser;
        }

        // Si aucun conflit, ajoute la période à tous les collaborateurs
        for (User user : period.getCollaborators()) {
            if (userPeriodsHashMap.containsKey(user)) {
                userPeriodsHashMap.get(user).getKey().add(period);
            }
        }
        return null; // Aucun conflit
    }

    // Vérifie les conflits de périodes pour chaque utilisateur
    public static User checkAvailability(LocalDate date, LocalTime startTime, LocalTime endTime, List<User> collaborators, Period ignoredPeriod) {
        for (User user : collaborators) {
            if (userPeriodsHashMap.containsKey(user)) {
                List<Period> userPeriods = userPeriodsHashMap.get(user).getKey();
                for (Period userPeriod : userPeriods) {
                    // Vérifie si les périodes se chevauchent
                    if (userPeriod != ignoredPeriod && userPeriod.getDate().isEqual(date) &&
                            ((endTime.isAfter(userPeriod.getStartTime()) && endTime.isBefore(userPeriod.getEndTime())) ||
                            (startTime.isAfter(userPeriod.getStartTime()) && startTime.isBefore(userPeriod.getEndTime())) ||
                            (startTime.isBefore(userPeriod.getStartTime()) && endTime.isAfter(userPeriod.getEndTime())))
                    ) {
                        return user; // Retourne l'utilisateur en conflit
                    }
                }
            }
        }
        return null;
    }

    public static User checkAvailability(LocalDate date, LocalTime startTime, LocalTime endTime, List<User> collaborators) {
        return checkAvailability(date, startTime, endTime, collaborators, null);
    }

    // Met à jour le date et heure d'une période
    public static User updatePeriodTime(Period period, LocalDate date, LocalTime startTime, LocalTime endTime) {
        User unavailableUser = checkAvailability(date, startTime, endTime, period.getCollaborators(), period);
        if (unavailableUser != null) {
            return unavailableUser;
        }
        period.setDate(date);
        period.setStartTime(startTime);
        period.setEndTime(endTime);
        return null;
    }

    // Supprime une période pour tous les collaborateurs associés
    public static void removePeriod(Period period) {
        for (User user : period.getCollaborators()) {
            if (userPeriodsHashMap.containsKey(user)) {
                userPeriodsHashMap.get(user).getKey().remove(period);
            }
        }
    }

    // Ajoute un type de période à un utilisateur
    public static boolean addPeriodTypeToUser(String title, Color color, Duration timeObjective, User user) {
        PeriodType periodType = new PeriodType(title, color, timeObjective);
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getValue().add(periodType);
            return true;
        }
        return false;
    }

    // Supprime un type de période d’un utilisateur
    public static boolean removePeriodTypeFromUser(PeriodType periodType, User user) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getValue().remove(periodType);
            return true;
        }
        return false;
    }

    // Retourne l'utilisateur actuellement connecté
    public static User getConnectedUser() {
        return connectedUser;
    }

    // Tente de connecter un utilisateur avec nom d'utilisateur et mot de passe
    public static boolean connectUser(String userName, String password) {
        User userToConnect = getUser(userName);
        if (userToConnect == null || !userToConnect.getPassword().equals(password)) {
            return false;
        } else {
            connectedUser = userToConnect;
            return true;
        }
    }

    public static User disconnectUser() {
        User userToDisconnect = connectedUser;
        connectedUser = null;
        return userToDisconnect;
    }

    public static boolean deleteUser(User user) {
        if (user.equals(connectedUser)) {
            return false;
        }
        return users.remove(user);
    }

    // Ajoute un nouvel utilisateur s'il n'existe pas déjà
    public static boolean addNewUser(String username, String password, String firstName, String lastName) {
        User previousUser = getUser(username);
        if (previousUser != null) {
            return false;
        }

        User newUser = new User(username, password, firstName, lastName);
        users.add(newUser);

        // Initialise avec une liste vide de périodes et un type de période "Base"
        userPeriodsHashMap.put(
                newUser,
                new Pair<>(FXCollections.observableArrayList(), FXCollections.observableArrayList(
                        new PeriodType("Base", Color.CYAN, Duration.ZERO)))
        );
        return true;
    }

    // Supprime un utilisateur
    public static boolean removeUser(User user) {
        if (!users.remove(user)) {
            return false;
        }
        userPeriodsHashMap.remove(user);
        return true;
    }

    // Récupère un utilisateur par son nom d’utilisateur
    public static User getUser(String userName) {
        return users.stream()
                .filter(user -> user.getUsername().equals(userName))
                .findFirst()
                .orElse(null);
    }

    // Retourne la liste observable de tous les utilisateurs
    public static ObservableList<User> getUsers() {
        return users;
    }

    // Retourne la liste observable des périodes d’un utilisateur
    public static ObservableList<Period> getPeriodsOfUser(User user) {
        return userPeriodsHashMap.get(user).getKey();
    }

    // Retourne la liste observable des types de période d’un utilisateur
    public static ObservableList<PeriodType> getPeriodTypesOfUser(User user) {
        return userPeriodsHashMap.get(user).getValue();
    }

    // Met à jour les objectifs de type de période avec le temps passé
    private static void updateObjectives(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        Duration timeIncrement = Duration.ofSeconds(newValue.longValue() - oldValue.longValue());
        for (Period period : getPeriodsOfUser(getConnectedUser())) {
            // Si la période est en cours actuellement, ajoute la durée au suivi de l’objectif
            if (LocalDate.now().isEqual(period.getDate()) &&
                    LocalTime.now().isAfter(period.getStartTime()) &&
                    LocalTime.now().isBefore(period.getEndTime())
            ) {
                period.getPeriodType().updateCompletedTimeObjective(timeIncrement);
            }
        }
    }
}
