package com.ahmet.barberbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.ahmet.barberbooking.Adapter.ViewPagerAllBookingAdapter;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AllBookingActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    @BindView(R.id.view_Pager_booking)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_booking);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Booking");

        mUnbinder = ButterKnife.bind(this);

        mViewPager.setAdapter(new ViewPagerAllBookingAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0)
                .setText("Current Booking");
        mTabLayout.getTabAt(1)
                .setText("History");




    }


}
