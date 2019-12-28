package com.ahmet.barberbooking.Adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ahmet.barberbooking.Fragments.CurrentBookingDialog;
import com.ahmet.barberbooking.Fragments.CurrentBookingFragment;
import com.ahmet.barberbooking.Fragments.HistoryFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAllBookingAdapter extends FragmentPagerAdapter {


    private List<String> mListTitle;
    private List<Fragment> mListFragments;

    public ViewPagerAllBookingAdapter(FragmentManager fm) {
        super(fm);
        mListTitle = new ArrayList<>();
        mListFragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {

        return mListFragments.get(position);
    }

    @Override
    public int getCount() {
        return mListFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mListTitle.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        mListFragments.add(fragment);
        mListTitle.add(title);
    }
}
