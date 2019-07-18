package com.ahmet.barberbooking.Interface;

import com.ahmet.barberbooking.Model.TimeSlot;

import java.util.List;

public interface iTimeSlotLoadListener {

    void onTimeSoltLoadSuccess(List<TimeSlot> mListTimeSlot);

    void onTimeSoltLoadFailed(String error);

    void onTimeSoltLoadEmpty();
}
