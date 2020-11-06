package com.scolabs.appiou.models;

public class Location {
    private double longitude;
    private double latitude;

    public Location() {

    }

    public Location(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
