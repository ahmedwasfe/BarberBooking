package com.ahmet.barberbooking.Interface;

import com.ahmet.barberbooking.Model.Banner;

import java.util.List;

public interface ILookBookLoadListener {

    void onLoadLookBookSuccess(List<Banner> mListLookBook);
    void onLoadLookBookFailed(String error);
}
