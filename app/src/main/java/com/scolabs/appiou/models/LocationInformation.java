package com.scolabs.appiou.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class LocationInformation implements Parcelable {
    private Location location;
    private String city;

    public LocationInformation() {

    }

    public LocationInformation(Location location, String city) {
        this.location = location;
        this.city = city;
    }

    protected LocationInformation(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        city = in.readString();
    }

    public static final Creator<LocationInformation> CREATOR = new Creator<LocationInformation>() {
        @Override
        public LocationInformation createFromParcel(Parcel in) {
            return new LocationInformation(in);
        }

        @Override
        public LocationInformation[] newArray(int size) {
            return new LocationInformation[size];
        }
    };

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(location, i);
        parcel.writeString(city);
    }
}
