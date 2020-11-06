package com.scolabs.appiou.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private String surname;
    private String uui;
    private String email;

    public User() {

    }

    public User(String name, String surname, String uui, String email) {
        this.name = name;
        this.surname = surname;
        this.uui = uui;
        this.email = email;
    }

    protected User(Parcel in) {
        name = in.readString();
        surname = in.readString();
        uui = in.readString();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUui() {
        return uui;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(surname);
        parcel.writeString(uui);
        parcel.writeString(email);
    }
}
