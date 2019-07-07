package com.ahmet.barberbooking.Interface;

import com.ahmet.barberbooking.Model.BookingInformation;

public interface IBookingInfoLoadListener {

    void onBookingInfoLoadSuccess(BookingInformation bookingInfo);
    void onBookingInfoLoadFailed(String error);
    void onBookingInfoLoadEmpty();
}
