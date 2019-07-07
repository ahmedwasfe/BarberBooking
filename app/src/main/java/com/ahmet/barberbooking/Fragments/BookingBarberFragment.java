package com.ahmet.barberbooking.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.ahmet.barberbooking.Adapter.BarberAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SpacesItemDecoration;
import com.ahmet.barberbooking.Model.Barber;
import com.ahmet.barberbooking.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingBarberFragment extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.recycler_barbers)
    RecyclerView mRecyclerBarbers;

    private LocalBroadcastManager mLocalBroadcastManager;

    private BroadcastReceiver barberDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<Barber> mListBarbers = intent.getParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE);

            BarberAdapter mBarberAdapter = new BarberAdapter(getActivity(), mListBarbers);
            mRecyclerBarbers.setAdapter(mBarberAdapter);
        }
    };

    static BookingBarberFragment instance;

    public static BookingBarberFragment getInstance(){
        if (instance == null){
            instance = new BookingBarberFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mLocalBroadcastManager.registerReceiver(barberDoneReceiver, new IntentFilter(Common.KEY_BARBER_LOAD_DONE));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_booking_barber, container, false);

        unbinder = ButterKnife.bind(this, layoutView);

        initView();

        return layoutView;
    }


    private void initView() {

        mRecyclerBarbers.setHasFixedSize(true);
        mRecyclerBarbers.setLayoutManager(new StaggeredGridLayoutManager(
                2, LinearLayout.VERTICAL));
        mRecyclerBarbers.addItemDecoration(new SpacesItemDecoration(4));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(barberDoneReceiver);
        super.onDestroy();
    }
}
