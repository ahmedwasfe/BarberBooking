package com.ahmet.barberbooking.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Salon implements Parcelable {

    private String name, address,website, phone, openHour, salonID;

    public Salon(){}

    public Salon(String name, String address, String salonID) {
        this.name = name;
        this.address = address;
        this.salonID = salonID;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSalonID() {
        return salonID;
    }

    public void setSalonID(String salonID) {
        this.salonID = salonID;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenHour() {
        return openHour;
    }

    public void setOpenHour(String openHour) {
        this.openHour = openHour;
    }

    protected Salon(Parcel in) {
        name = in.readString();
        address = in.readString();
        salonID = in.readString();
        website = in.readString();
        phone = in.readString();
        openHour = in.readString();
    }

    public static final Creator<Salon> CREATOR = new Creator<Salon>() {
        @Override
        public Salon createFromParcel(Parcel in) {
            return new Salon(in);
        }

        @Override
        public Salon[] newArray(int size) {
            return new Salon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(salonID);
        dest.writeString(website);
        dest.writeString(phone);
        dest.writeString(openHour);
    }
}
