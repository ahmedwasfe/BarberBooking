package com.ahmet.barberbooking.SubActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.ahmet.barberbooking.Adapter.ViewPagerAllBookingAdapter;
import com.ahmet.barberbooking.Fragments.CurrentBookingFragment;
import com.ahmet.barberbooking.Fragments.HistoryFragment;
import com.ahmet.barberbooking.R;
import com.google.android.material.tabs.TabLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AllBookingActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    @BindView(R.id.view_Pager_booking)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    SmartTabLayout mTabLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_booking);


        mUnbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.your_booking);

       // mViewPager.setAdapter(new ViewPagerAllBookingAdapter(getSupportFragmentManager()));
        ViewPagerAllBookingAdapter viewPagerAllBookingAdapter = new ViewPagerAllBookingAdapter(getSupportFragmentManager());
        viewPagerAllBookingAdapter.addFragment(CurrentBookingFragment.getInstance(),getString(R.string.current_booking));
        viewPagerAllBookingAdapter.addFragment(HistoryFragment.getInstance(),getString(R.string.history));

        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(viewPagerAllBookingAdapter);

        mTabLayout.setViewPager(mViewPager);
        mTabLayout.getTabAt(0)
                .setTooltipText(getString(R.string.current_booking));
        mTabLayout.getTabAt(1)
                .setTooltipText(getString(R.string.history));




    }


}
