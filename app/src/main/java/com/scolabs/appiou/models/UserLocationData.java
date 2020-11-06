package com.scolabs.appiou.models;

public class UserLocationData {
    private User user;
    private Location location;

    public UserLocationData() {

    }

    public UserLocationData(User user, Location location) {
        this.user = user;
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public Location getLocation() {
        return location;
    }
}
