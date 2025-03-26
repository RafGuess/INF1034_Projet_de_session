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
    private static User connectedUser;
    final private static ObservableList<User> users = FXCollections.observableArrayList();
    final private static HashMap<User, Pair<ObservableList<Period>, ObservableList<PeriodType>>> userPeriodsHashMap = new HashMap<>();

    static {
        Timer.addListener(Database::updateObjectives);
    }

    static {
        // test data
        addNewUser("admin", "1234", "Bob", "Bricoleur");
        addNewUser("superSlayer3000", "superPassword", "Joe", "LeFou");
        addNewUser("whatDisAppAbout", "no", "YouWonTGet", "MyName");
        connectedUser = getUser("admin");

        addPeriodTypeToUser("test", Color.CYAN, Duration.ofHours(3), getConnectedUser());
        addPeriodTypeToUser("test2", Color.GREEN, Duration.ofHours(2), getConnectedUser());

        ArrayList<User> users = new ArrayList<>();
        users.add(connectedUser);

        addPeriod(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(2),
                getPeriodTypesOfUser(getConnectedUser()).get(1), "very cool notes", users);
        addPeriod(LocalDate.now(), LocalTime.now().minusHours(6), LocalTime.now().minusHours(4),
                getPeriodTypesOfUser(getConnectedUser()).get(2), "very cool notes again", users);
    }

    public static boolean addListenerToPeriodsOfUser(User user, ListChangeListener<Period> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getKey().addListener(listener);
            return true;
        }
        return false;
    }

    public static boolean removeListenerFromPeriodsOfUser(User user, ListChangeListener<Period> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getKey().removeListener(listener);
            return true;
        }
        return false;
    }

    public static boolean addListenerToPeriodTypesOfUser(User user, ListChangeListener<PeriodType> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getValue().addListener(listener);
            return true;
        }
        return false;
    }

    public static boolean removeListenerToPeriodTypesOfUser(User user, ListChangeListener<PeriodType> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getValue().removeListener(listener);
            return true;
        }
        return false;
    }

    public static User addPeriod(LocalDate date, LocalTime startTime, LocalTime endTime, PeriodType periodType, String notes, List<User> collaborators) {
        Period period = new Period(date, startTime, endTime, periodType, notes, collaborators);

        for (User user : period.getCollaborators()) {
            if (userPeriodsHashMap.containsKey(user)) {
                List<Period> userPeriods = userPeriodsHashMap.get(user).getKey();

                for (Period userPeriod : userPeriods) {
                    if (userPeriod.getDate().isEqual(date) &&
                            ((endTime.isAfter(userPeriod.getStartTime()) && endTime.isBefore(userPeriod.getEndTime())) ||
                            (startTime.isAfter(userPeriod.getStartTime()) && startTime.isBefore(userPeriod.getEndTime())))
                    ) {
                        return user;
                    }
                }
            }
        }
        // all users are available

        for (User user : period.getCollaborators()) {
            if (userPeriodsHashMap.containsKey(user)) {
                userPeriodsHashMap.get(user).getKey().add(period);
            }
        }
        return null;
    }

    public static void removePeriod(Period period) {
        for (User user : period.getCollaborators()) {
            if (userPeriodsHashMap.containsKey(user)) {
                userPeriodsHashMap.get(user).getKey().remove(period);
            }
        }
    }

    public static boolean addPeriodTypeToUser(String title, Color color, Duration timeObjective, User user) {
        PeriodType periodType = new PeriodType(title, color, timeObjective);
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getValue().add(periodType);
            return true;
        }
        return false;
    }

    public static boolean removePeriodTypeFromUser(PeriodType periodType, User user) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).getValue().remove(periodType);
            return true;
        }
        return false;
    }


    public static User getConnectedUser() {
        return connectedUser;
    }

    public static boolean connectUser(String userName, String password) {
        User userToConnect = getUser(userName);

        if (userToConnect == null || !userToConnect.getPassword().equals(password)) {
            return false;
        } else {
            connectedUser = userToConnect;
            return true;
        }
    }

    public static boolean addNewUser(String username, String password, String firstName, String lastName) {
        User previousUser = getUser(username);
        if (previousUser != null) {
            return false;
        }
        User newUser = new User(username, password, firstName, lastName);

        users.add(newUser);
        userPeriodsHashMap.put(
                newUser,
                new Pair<>(FXCollections.observableArrayList(), FXCollections.observableArrayList(
                        new PeriodType("Base", Color.CYAN, Duration.ZERO)))
        );
        return true;
    }

    public static boolean removeUser(User user) {
        if(!users.remove(user)) {
            return false;
        }
        userPeriodsHashMap.remove(user);
        return true;
    }

    public static User getUser(String userName) {
        return users.stream()
                .filter(user -> user.getUsername().equals(userName))
                .findFirst()
                .orElse(null);
    }

    public static ObservableList<User> getUsers() {
        return users;
    }


    public static ObservableList<Period> getPeriodsOfUser(User user) {
        return userPeriodsHashMap.get(user).getKey();
    }

    public static ObservableList<PeriodType> getPeriodTypesOfUser(User user) {
        return userPeriodsHashMap.get(user).getValue();
    }

    private static void updateObjectives(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        Duration timeIncrement = Duration.ofSeconds(newValue.longValue() - oldValue.longValue());
        for (Period period : getPeriodsOfUser(getConnectedUser())) {
            if (LocalDate.now().isEqual(period.getDate()) &&
                LocalTime.now().isAfter(period.getStartTime()) &&
                LocalTime.now().isBefore(period.getEndTime())
            ) {
                period.getPeriodType().updateCompletedTimeObjective(timeIncrement);
            }
        }
    }
}
