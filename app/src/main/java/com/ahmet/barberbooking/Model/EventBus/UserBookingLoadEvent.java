package com.ahmet.barberbooking.Model.EventBus;

import com.ahmet.barberbooking.Fragments.BookingConfirmFragment;
import com.ahmet.barberbooking.Model.BookingInformation;

import java.util.List;

public class UserBookingLoadEvent {

    private boolean isloaded;
    private String error;
    private List<BookingInformation> mListBookingInfo;

    public UserBookingLoadEvent(boolean isloaded, String error) {
        this.isloaded = isloaded;
        this.error = error;

    }

    public UserBookingLoadEvent(boolean isloaded, List<BookingInformation> mListBookingInfo) {
        this.isloaded = isloaded;
        this.mListBookingInfo = mListBookingInfo;
    }

    public boolean isIsloaded() {
        return isloaded;
    }

    public void setIsloaded(boolean isloaded) {
        this.isloaded = isloaded;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<BookingInformation> getmListBookingInfo() {
        return mListBookingInfo;
    }

    public void setmListBookingInfo(List<BookingInformation> mListBookingInfo) {
        this.mListBookingInfo = mListBookingInfo;
    }
}
