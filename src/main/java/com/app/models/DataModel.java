package com.app.models;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class DataModel {
    private static User connectedUser = null;
    final private static HashMap<User, ObservableList<Period>> userPeriodsHashMap = new HashMap<>();

    public static boolean addListenerToPeriodsOfUser(User user, ListChangeListener<Period> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).addListener(listener);
            return true;
        }
        return false;
    }

    public static boolean removeListenerFromPeriodsOfUser(User user, ListChangeListener<Period> listener) {
        if (userPeriodsHashMap.containsKey(user)) {
            userPeriodsHashMap.get(user).removeListener(listener);
            return true;
        }
        return false;
    }

    public static void addPeriodToUsers(Period period, User... users) {
        for (User user : users) {
            if (!userPeriodsHashMap.containsKey(user)) {
                userPeriodsHashMap.get(user).add(period);
            }
        }
    }

    public static void removePeriodFromUsers(Period period, User... users) {
        for (User user : users) {
            if (userPeriodsHashMap.containsKey(user)) {
                userPeriodsHashMap.get(user).remove(period);
            }
        }
    }

    public static void setConnectedUser(User connectedUser) {
        DataModel.connectedUser = connectedUser;
    }

    public static boolean addNewUser(String username, String password) {
        return null == userPeriodsHashMap.putIfAbsent(new User(username, password), FXCollections.observableArrayList());
    }

    public static boolean removeUser() {
        return null != userPeriodsHashMap.remove(connectedUser);
    }
}
