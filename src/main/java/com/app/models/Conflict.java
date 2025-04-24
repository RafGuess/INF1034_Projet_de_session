package com.app.models;

public class Conflict {
    private final User unavailableUser;
    private final Period period;
    public static final Conflict NONE = new Conflict(null, null);

    public Conflict(User unavailableUser, Period periodInConflict) {
        this.unavailableUser = unavailableUser;
        this.period = periodInConflict;
    }

    public boolean isInConflict() {
        return period != null && unavailableUser != null;
    }

    public User getUnavailableUser() {
        return unavailableUser;
    }

    public Period getPeriod() {
        return period;
    }
}
