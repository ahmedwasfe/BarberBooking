package com.ahmet.barberbooking.Model;

import com.ahmet.barberbooking.Databse.CartItem;
import com.google.firebase.Timestamp;

import java.util.List;

public class BookingInformation {

    private String customerName, customerPhone, time,
                    barberID, barberName, salonID,
                    salonName, salonAddress, cityBooking;

    private Long timeSlot;
    private Timestamp timestamp;
    private boolean done;
    private List<CartItem> mListCart;

    public BookingInformation() {}

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBarberID() {
        return barberID;
    }

    public void setBarberID(String barberID) {
        this.barberID = barberID;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getSalonID() {
        return salonID;
    }

    public void setSalonID(String salonID) {
        this.salonID = salonID;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getSalonAddress() {
        return salonAddress;
    }

    public void setSalonAddress(String salonAddress) {
        this.salonAddress = salonAddress;
    }

    public Long getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Long timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getCityBooking() {
        return cityBooking;
    }

    public void setCityBooking(String cityBooking) {
        this.cityBooking = cityBooking;
    }

    public List<CartItem> getmListCart() {
        return mListCart;
    }

    public void setmListCart(List<CartItem> mListCart) {
        this.mListCart = mListCart;
    }
}
