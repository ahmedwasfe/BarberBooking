package com.ahmet.barberbooking.Interface;

import android.view.View;

public interface IRecyclerItemSelectedListener {

    void onItemSelectedListener(View view , int position);

    void onItemNotSelected(int position);
}
