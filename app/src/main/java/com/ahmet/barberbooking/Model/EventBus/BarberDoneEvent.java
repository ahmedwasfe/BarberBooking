package com.ahmet.barberbooking.Model.EventBus;

import com.ahmet.barberbooking.Model.Barber;

import java.util.List;

public class BarberDoneEvent {

    private List<Barber> mListBarber;

    public BarberDoneEvent(List<Barber> mListBarber) {
        this.mListBarber = mListBarber;
    }

    public List<Barber> getmListBarber() {
        return mListBarber;
    }

    public void setmListBarber(List<Barber> mListBarber) {
        this.mListBarber = mListBarber;
    }
}
