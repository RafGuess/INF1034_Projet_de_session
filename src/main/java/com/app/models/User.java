package com.app.models;

public class User {

    // Nom d'utilisateur (identifiant unique)
    private String username;

    // Mot de passe (non sécurisé en l'état — à éviter en production)
    private String password;

    // Prénom de l'utilisateur
    private String firstName;

    // Nom de famille de l'utilisateur
    private String lastName;

    // Constructeur
    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters et setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        System.out.println(username.hashCode());
        this.username = username;
        System.out.println(username.hashCode());
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Redéfinition de equals : comparaison basée uniquement sur le nom d'utilisateur
    @Override
    public boolean equals(Object o) {
        if (o instanceof User user) {
            return username.equals(user.getUsername());
        }
        return false;
    }

    // Représentation texte de l'utilisateur (utilisée dans les ComboBox, logs, etc.)
    @Override
    public String toString() {
        return String.format("%s %s", firstName, lastName);
    }
}
