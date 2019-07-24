package com.ahmet.barberbooking.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Barber implements Parcelable {

    private String name, username, password, barberID;
    private Double rating;
    private Long ratingTimes;

    public Barber() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBarberID() {
        return barberID;
    }

    public void setBarberID(String barberID) {
        this.barberID = barberID;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getRatingTimes() {
        return ratingTimes;
    }

    public void setRatingTimes(Long ratingTimes) {
        this.ratingTimes = ratingTimes;
    }

    protected Barber(Parcel in) {

        name = in.readString();
        username = in.readString();
        password = in.readString();
        barberID = in.readString();

        if (in.readByte() == 0)
            rating = null;
        else {
            rating = in.readDouble();
            ratingTimes = in.readLong();
        }

    }

    public static final Creator<Barber> CREATOR = new Creator<Barber>() {
        @Override
        public Barber createFromParcel(Parcel in) {
            return new Barber(in);
        }

        @Override
        public Barber[] newArray(int size) {
            return new Barber[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(barberID);

        if (rating == null)
            dest.writeByte((byte) 0);
        else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
            dest.writeLong(ratingTimes);
        }
    }
}
