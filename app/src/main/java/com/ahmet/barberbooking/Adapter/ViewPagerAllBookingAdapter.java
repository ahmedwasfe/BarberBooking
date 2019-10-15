package com.ahmet.barberbooking.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ahmet.barberbooking.Fragments.CurrentBookingDialog;
import com.ahmet.barberbooking.Fragments.CurrentBookingFragment;
import com.ahmet.barberbooking.Fragments.HistoryFragment;

public class ViewPagerAllBookingAdapter extends FragmentPagerAdapter {


    public ViewPagerAllBookingAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return CurrentBookingFragment.getInstance();
            case 1:
                return HistoryFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
