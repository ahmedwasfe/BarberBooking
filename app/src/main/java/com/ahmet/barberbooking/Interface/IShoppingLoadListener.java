package com.ahmet.barberbooking.Interface;

import com.ahmet.barberbooking.Model.Shopping;

import java.util.List;

public interface IShoppingLoadListener {

    void onShoppingLoadSuccess(List<Shopping> mListShopping);
    void onShoppingLoadFailed(String error);
}
