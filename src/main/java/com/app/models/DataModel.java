package com.app.models;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataModel {
    private static User connectedUser;
    final private static ObservableList<User> users = FXCollections.observableArrayList();
    final private static HashMap<User, Pair<ObservableList<Period>, ObservableList<PeriodType>>> userPeriodsHashMap = new HashMap<>();

    static {
        // test data
        addNewUser("admin", "1234", "Bob", "Bricoleur");
        addNewUser("superSlayer3000", "superPassword", "Joe", "LeFou");
        addNewUser("whatDisAppAbout", "no", "YouWonTGet", "MyName");
        connectedUser = getUser("admin");

        ArrayList<User> users = new ArrayList<>();
        users.add(connectedUser);
        Period period1 = new Period(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(2),
                new PeriodType("test", Color.CYAN), "very cool notes");
        Period period2 = new Period(LocalDate.now(), LocalTime.now().minusHours(6), LocalTime.now().minusHours(4),
                new PeriodType("test2", Color.GREEN), "very cool notes again");

        period1.addCollaborators(users);
        period2.addCollaborators(users);

        addPeriod(period1);
        addPeriod(period2);
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

    public static void addPeriod(Period period) {
        // todo: check if period CAN be added depending on date and time
        for (User user : period.getCollaborators()) {
            if (userPeriodsHashMap.containsKey(user)) {
                userPeriodsHashMap.get(user).getKey().add(period);
            }
        }
    }

    public static void removePeriod(Period period) {
        for (User user : period.getCollaborators()) {
            if (userPeriodsHashMap.containsKey(user)) {
                userPeriodsHashMap.get(user).getKey().remove(period);
            }
        }
    }

    public static boolean addPeriodTypeToUser(PeriodType periodType, User user) {
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
                        new PeriodType("Base", Color.CYAN)))
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
}
