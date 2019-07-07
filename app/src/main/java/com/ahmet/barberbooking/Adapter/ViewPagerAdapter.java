package com.ahmet.barberbooking.Adapter;

import com.ahmet.barberbooking.Fragments.BookingBarberFragment;
import com.ahmet.barberbooking.Fragments.BookingConfirmFragment;
import com.ahmet.barberbooking.Fragments.BookingSalonFragment;
import com.ahmet.barberbooking.Fragments.BookingTimeFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return BookingSalonFragment.getInstance();
            case 1:
                return BookingBarberFragment.getInstance();
            case 2:
                return BookingTimeFragment.getInstance();
            case 3:
                return BookingConfirmFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
