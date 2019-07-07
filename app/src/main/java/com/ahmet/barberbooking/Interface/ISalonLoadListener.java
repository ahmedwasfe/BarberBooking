package com.ahmet.barberbooking.Interface;

import com.ahmet.barberbooking.Model.Salon;

import java.util.List;

public interface ISalonLoadListener {

    void onLoadSalonSuccess(List<Salon> mListSalon);
    void onLoadSalonFailed(String error);

}
