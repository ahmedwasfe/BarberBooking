package com.ahmet.barberbooking.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.TimeSoltAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SpacesItemDecoration;
import com.ahmet.barberbooking.Interface.ITimeSoltLoadListener;
import com.ahmet.barberbooking.Model.TimeSlot;
import com.ahmet.barberbooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class BookingTimeFragment extends Fragment implements ITimeSoltLoadListener {


    private Unbinder unbinder;

    @BindView(R.id.recycler_time_solt)
    RecyclerView mRecyclerTimeSolt;
    @BindView(R.id.calender_time_solt)
    HorizontalCalendarView mCalendarDateView;

    private DocumentReference mDocReferenceBarber;

    private ITimeSoltLoadListener iTimeSoltLoadLitener;

    private AlertDialog mDialog;

    private LocalBroadcastManager mLocalBroadcastManager;

    // private Calendar selectedDate;
    private SimpleDateFormat mSimpleDateFormat;


    BroadcastReceiver displayTimeSolt = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, 0);  // Add current date

            loadAvailableTimeSoltOfBarber(Common.currentBarber.getBarberID(),
                                          mSimpleDateFormat.format(date.getTime()));
        }
    };


    static BookingTimeFragment instance;

    public static BookingTimeFragment getInstance(){

        if (instance == null){
            instance = new BookingTimeFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTimeSoltLoadLitener = this;

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mLocalBroadcastManager.registerReceiver(displayTimeSolt, new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));

        mSimpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");  // 27_06_2019 (this is key)

        mDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .build();

//        selectedDate = Calendar.getInstance();
//        selectedDate.add(Calendar.DATE, 0);  // init current date
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_booking_time, container, false);

        unbinder = ButterKnife.bind(this, layoutView);

        // initViews
        initViews(layoutView);

        return layoutView;
    }


    private void loadAvailableTimeSoltOfBarber(String barberID, String date) {

        mDialog.show();

        // /AllSalon/Gaza/Branch/AFXjgtlJwztf7cLFumNT/Barber/utQmhc07WVjaZdr9tbRB
        mDocReferenceBarber = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.city)
                .collection("Branch")
                .document(Common.currentSalon.getSalonID())
                .collection("Barber")
                .document(barberID);

        // Get informatio for this barber
        mDocReferenceBarber.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){

                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){ // If babrber available

                                // Get information of booking
                                // If not created return empty
                                CollectionReference mReferenceDate =  FirebaseFirestore.getInstance()
                                        .collection("AllSalon")
                                        .document(Common.city)
                                        .collection("Branch")
                                        .document(Common.currentSalon.getSalonID())
                                        .collection("Barber")
                                        .document(barberID)
                                        .collection(date);  // book date is date simpleformat with dd_MM_yyyy == 27_06_2019

                                mReferenceDate.get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                if (task.isSuccessful()){

                                                    QuerySnapshot querySnapshot = task.getResult();
                                                    if (querySnapshot.isEmpty()){  // If do not have any appoment

                                                        iTimeSoltLoadLitener.onTimeSoltLoadEmpty();
                                                    }else {
                                                        // If have appoiment
                                                        List<TimeSlot> mListTimeSlot = new ArrayList<>();
                                                        for (QueryDocumentSnapshot snapshot : task.getResult())
                                                            mListTimeSlot.add(snapshot.toObject(TimeSlot.class));

                                                        iTimeSoltLoadLitener.onTimeSoltLoadSuccess(mListTimeSlot);
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        iTimeSoltLoadLitener.onTimeSoltLoadFailed(e.getMessage());
                                    }
                                });
                            }
                        }
                    }
                });


    }

    private void initViews(View layoutView) {

        // Recycler View
        // mRecyclerTimeSolt = layoutView.findViewById(R.id.recycler_time_solt);
        mRecyclerTimeSolt.setHasFixedSize(true);
        mRecyclerTimeSolt.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        mRecyclerTimeSolt.addItemDecoration(new SpacesItemDecoration(8));

        // HorizontalCalendarView
        mCalendarDateView = layoutView.findViewById(R.id.calender_time_solt);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 2);  // 2 day left

        HorizontalCalendar mCalendarDate = new HorizontalCalendar.Builder(layoutView, R.id.calender_time_solt)
                .range(startDate, endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();

        mCalendarDate.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                if (Common.bookingDate.getTimeInMillis() != date.getTimeInMillis()){
                    Common.bookingDate = date;  // this code will not load again you selecte day same with day selected
                    loadAvailableTimeSoltOfBarber(Common.currentBarber.getBarberID(),
                            mSimpleDateFormat.format(date.getTime()));
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Toast.makeText(getActivity(), Common.currentSalon.getSalonID(), Toast.LENGTH_SHORT).show();

//        loadAvailableTimeSoltOfBarber(Common.currentBarber.getBarberID(),
//                                       mSimpleDateFormat.format(selectedDate.getTime()));
    }

    @Override
    public void onTimeSoltLoadSuccess(List<TimeSlot> mListTimeSlot) {

        TimeSoltAdapter mTimeSoltAdapter = new TimeSoltAdapter(getActivity(), mListTimeSlot);
        mRecyclerTimeSolt.setAdapter(mTimeSoltAdapter);

        mDialog.dismiss();
    }

    @Override
    public void onTimeSoltLoadFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        mDialog.dismiss();
    }

    @Override
    public void onTimeSoltLoadEmpty() {

        TimeSoltAdapter mTimeSoltAdapter = new TimeSoltAdapter(getActivity());
        mRecyclerTimeSolt.setAdapter(mTimeSoltAdapter);

        mDialog.dismiss();
    }

    @Override
    public void onDestroy() {

        mLocalBroadcastManager.unregisterReceiver(displayTimeSolt);

        super.onDestroy();
    }
}
