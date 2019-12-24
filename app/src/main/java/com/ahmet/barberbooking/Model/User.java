package com.ahmet.barberbooking.Model;

/**
 * Created by Android Studio.
 * User: ahmet
 * Date: 5/23/2019
 * Time: 11:47 AM
 */


public class User {

    private String
                name,
                address,
                phoneNumber;

    public User() {}

    public User(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
