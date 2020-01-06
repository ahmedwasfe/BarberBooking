package com.ahmet.barberbooking.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.TimeSlotAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SaveSettings;
import com.ahmet.barberbooking.Common.SpacesItemDecoration;
import com.ahmet.barberbooking.Interface.iTimeSlotLoadListener;
import com.ahmet.barberbooking.Model.EventBus.DisplayTimeSlotEvent;
import com.ahmet.barberbooking.Model.TimeSlot;
import com.ahmet.barberbooking.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

public class BookingTimeFragment extends Fragment implements iTimeSlotLoadListener {


    private Unbinder unbinder;

    @BindView(R.id.recycler_time_solt)
    RecyclerView mRecyclerTimeSolt;
    @BindView(R.id.calender_time_solt)
    HorizontalCalendarView mCalendarDateView;

    private DocumentReference mDocReferenceBarber;

    private iTimeSlotLoadListener iTimeSlotLoadListener;

    private AlertDialog mDialog;

   // private LocalBroadcastManager mLocalBroadcastManager;

    // private Calendar selectedDate;
    private SimpleDateFormat mSimpleDateFormat;


    /* old Code
    BroadcastReceiver displayTimeSolt = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, 0);  // Add current date

            loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberID(),
                                          mSimpleDateFormat.format(date.getTime()));
        }
    };
*/
    private SaveSettings mSaveSettings;

    // ---------------------------------------------------------------------
    // Start Event Bus

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadAllTimeSlotAvailable(DisplayTimeSlotEvent event){

        if (event.isDisplay()){

            // In Booking Activity we need have pass this event with isDisplay = true
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, 0);  // Add current date

            loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberID(),
                    mSimpleDateFormat.format(date.getTime()));
        }
    }

    // ---------------------------------------------------------------------

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

        iTimeSlotLoadListener = this;

        /* old Code
          *  mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
           * mLocalBroadcastManager.registerReceiver(displayTimeSolt, new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));
        */

        mSimpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");  // 27_06_2019 (this is key)

        mDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage(R.string.please_wait)
                .build();

//        selectedDate = Calendar.getInstance();
//        selectedDate.add(Calendar.DATE, 0);  // init current date
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mSaveSettings = new SaveSettings(getActivity());
        if (mSaveSettings.getNightModeState() == true)
            getActivity().setTheme(R.style.DarkTheme);
        else
            getActivity().setTheme(R.style.AppTheme);

        if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_EN))
            Common.setLanguage(getActivity(), Common.KEY_LANGUAGE_EN);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_AR))
            Common.setLanguage(getActivity(), Common.KEY_LANGUAGE_AR);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_TR))
            Common.setLanguage(getActivity(), Common.KEY_LANGUAGE_TR);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_FR))
            Common.setLanguage(getActivity(),Common.KEY_LANGUAGE_FR);


        View layoutView = inflater.inflate(R.layout.fragment_booking_time, container, false);

        unbinder = ButterKnife.bind(this, layoutView);

        // initViews
        initViews(layoutView);

        return layoutView;
    }


    private void loadAvailableTimeSlotOfBarber(String barberID, String bookingDate) {

        mDialog.show();

        // /AllSalon/Gaza/Branch/AFXjgtlJwztf7cLFumNT/Barber/utQmhc07WVjaZdr9tbRB
        mDocReferenceBarber = FirebaseFirestore.getInstance()
                .collection(Common.KEY_COLLECTION_AllSALON)
                .document(Common.currentSalon.getSalonID())
                .collection(Common.KEY_COLLECTION_BARBER)
                .document(barberID);

        // Get informatio for this barber
        mDocReferenceBarber.get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()){ // If babrber available

                            // Get information of booking
                            // If not created return empty
                            CollectionReference mReferenceDate =  FirebaseFirestore.getInstance()
                                    .collection(Common.KEY_COLLECTION_AllSALON)
                                    .document(Common.currentSalon.getSalonID())
                                    .collection(Common.KEY_COLLECTION_BARBER)
                                    .document(barberID)
                                    .collection(bookingDate);  // book date is date simpleformat with dd_MM_yyyy == 27_06_2019

                            mReferenceDate.get()
                                    .addOnCompleteListener(task1 -> {

                                        if (task1.isSuccessful()){

                                            QuerySnapshot querySnapshot = task1.getResult();
                                            if (querySnapshot.isEmpty()){  // If do not have any appoment

                                                iTimeSlotLoadListener.onTimeSoltLoadEmpty();
                                            }else {
                                                // If have appoiment
                                                List<TimeSlot> mListTimeSlot = new ArrayList<>();
                                                for (QueryDocumentSnapshot snapshot : task1.getResult())
                                                    mListTimeSlot.add(snapshot.toObject(TimeSlot.class));

                                                iTimeSlotLoadListener.onTimeSoltLoadSuccess(mListTimeSlot);
                                            }
                                        }
                                    }).addOnFailureListener(e -> iTimeSlotLoadListener.onTimeSoltLoadFailed(e.getMessage()));
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
                    loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberID(),
                            mSimpleDateFormat.format(date.getTime()));
                }
            }
        });
    }

    @Override
    public void onTimeSoltLoadSuccess(List<TimeSlot> mListTimeSlot) {

        TimeSlotAdapter mTimeSlotAdapter = new TimeSlotAdapter(getActivity(), mListTimeSlot);
        mRecyclerTimeSolt.setAdapter(mTimeSlotAdapter);

        mDialog.dismiss();
    }

    @Override
    public void onTimeSoltLoadFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        mDialog.dismiss();
    }

    @Override
    public void onTimeSoltLoadEmpty() {

        TimeSlotAdapter mTimeSlotAdapter = new TimeSlotAdapter(getActivity());
        mRecyclerTimeSolt.setAdapter(mTimeSlotAdapter);

        mDialog.dismiss();
    }

//    @Override
//    public void onDestroy() {
//        mLocalBroadcastManager.unregisterReceiver(displayTimeSolt);
//        super.onDestroy();
//    }
}
