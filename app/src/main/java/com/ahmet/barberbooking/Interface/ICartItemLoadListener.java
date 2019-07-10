package com.ahmet.barberbooking.Interface;

import com.ahmet.barberbooking.Databse.CartItem;

import java.util.List;

public interface ICartItemLoadListener {

    void onLoadAllItemInCartSuccess(List<CartItem> mListCartItem);
}
