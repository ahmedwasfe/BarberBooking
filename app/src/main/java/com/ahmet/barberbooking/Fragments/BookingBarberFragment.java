package com.ahmet.barberbooking.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.BarberAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SpacesItemDecoration;
import com.ahmet.barberbooking.Model.Barber;
import com.ahmet.barberbooking.Model.EventBus.BarberDoneEvent;
import com.ahmet.barberbooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingBarberFragment extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.recycler_barbers)
    RecyclerView mRecyclerBarbers;

    /* old Code
   // private LocalBroadcastManager mLocalBroadcastManager;

    private BroadcastReceiver barberDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<Barber> mListBarbers = intent.getParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE);

            BarberAdapter mBarberAdapter = new BarberAdapter(getActivity(), mListBarbers);
            mRecyclerBarbers.setAdapter(mBarberAdapter);
        }
    };
    */

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
    public void setBarberAdapter(BarberDoneEvent event){

        BarberAdapter mBarberAdapter = new BarberAdapter(getActivity(), event.getmListBarber());
        mRecyclerBarbers.setAdapter(mBarberAdapter);
    }

    // ---------------------------------------------------------------------

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

        /* Old Code
        *
        * mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        * mLocalBroadcastManager.registerReceiver(barberDoneReceiver, new IntentFilter(Common.KEY_BARBER_LOAD_DONE));
        */
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_booking_barber, container, false);

        unbinder = ButterKnife.bind(this, layoutView);

        initView();

      //  loadBarbersBySalon();

        return layoutView;
    }


    private void initView() {

        mRecyclerBarbers.setHasFixedSize(true);
        mRecyclerBarbers.setLayoutManager(new StaggeredGridLayoutManager(
                2, LinearLayout.VERTICAL));
        mRecyclerBarbers.addItemDecoration(new SpacesItemDecoration(4));
    }


    private void loadBarbersBySalon() {

        //mDialog.show();

        if (!TextUtils.isEmpty(Common.currentSalon.getSalonID())){

            FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentSalon.getSalonID())
                    .collection("Barber")
            // Query query = mReferenceBarbers.orderBy("name", Query.Direction.ASCENDING);
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            List<Barber> mListBarber = new ArrayList<>();


                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                Barber barber = snapshot.toObject(Barber.class);
                                barber.setPassword(""); // remove password because in Clinte app ||
                                barber.setBarberID(snapshot.getId());

                                mListBarber.add(barber);
                            }

                            /* Old Code
                             * Send Broadcast to barber Fragment loadAllBaber
                             * Intent intent = new Intent(Common.KEY_BARBER_LOAD_DONE);
                             * intent.putParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE, (ArrayList<? extends Parcelable>) mListBarber);
                             * mLocalBroadcastManager.sendBroadcast(intent);
                             */

                            BarberAdapter mBarberAdapter = new BarberAdapter(getActivity(), mListBarber);
                            mRecyclerBarbers.setAdapter(mBarberAdapter);

                            EventBus.getDefault()
                                    .postSticky(new BarberDoneEvent(mListBarber));





                           // mDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   // mDialog.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }
}
