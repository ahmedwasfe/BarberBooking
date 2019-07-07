package com.ahmet.barberbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.ViewPagerAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.NonSwipeViewPager;
import com.ahmet.barberbooking.Model.Barber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    @BindView(R.id.step_view)
    StepView mStepView;
    @BindView(R.id.view_pager_steps)
    NonSwipeViewPager mViewPagerSteps;
    @BindView(R.id.btn_previous_step)
    Button mBtnPreviousStep;
    @BindView(R.id.btn_next_step)
    Button mBtnNextStep;

    private LocalBroadcastManager mLocalBroadcastManager;

    private AlertDialog mDialog;
    private CollectionReference mReferenceBarbers;

    // Event
    @OnClick(R.id.btn_previous_step)
    void previousStep(){

        if (Common.setp == 3 || Common.setp > 0){

            Common.setp--;
            mViewPagerSteps.setCurrentItem(Common.setp);

            // Always enable NEXT when step 3
            if (Common.setp < 3){
                mBtnNextStep.setEnabled(true);
                setColorStepButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextStep(){

        Toast.makeText(this, "" + Common.currentSalon.getSalonID(), Toast.LENGTH_SHORT).show();

        if(Common.setp < 3 || Common.setp == 0){

//            Toast.makeText(this, "" + Common.currentSalon.getSalonID(), Toast.LENGTH_SHORT).show();
           // Log.d("SalonID", Common.currentSalon.getSalonID());

            Common.setp++; // Increase
            if (Common.setp == 1){ // After choose salon
                Toast.makeText(this, "" + Common.currentSalon.getSalonID(), Toast.LENGTH_SHORT).show();
                loadBarbersBySalon(Common.currentSalon.getSalonID());

            } else if (Common.setp == 2){   // Pick time solt
                if (Common.currentBarber != null){

                    loadTimeSoltOfBarber(Common.currentBarber.getBarberID());
                }

            } else if (Common.setp == 3){   // Pick Confirm booking
                if (Common.currentTimeSlot != -1){
                    confirmBooking();
                }
            }
            mViewPagerSteps.setCurrentItem(Common.setp);
        }
    }

    private void confirmBooking() {

        // Send broadcast to Confirm Fragment
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSoltOfBarber(String barberID) {

        // Send Broadcast to Fragment Time
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
//        Common.currentSalon.getSalonID();
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    // Select all barber of salon
    // /AllSalon/Gaza/Branch/MeSLZt4RqjZvlv7gNwif/Barbers/fkesYLCIw6assFWcqNlK
    private void loadBarbersBySalon(String salonID) {

        mDialog.show();

        if (!TextUtils.isEmpty(Common.city)){

            mReferenceBarbers = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.city)
                    .collection("Branch")
                    .document(salonID)
                    .collection("Barber");
           // Query query = mReferenceBarbers.orderBy("name", Query.Direction.ASCENDING);

            mReferenceBarbers.get()
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
                            // Send Broadcast to barber Fragment loadAllBaber
                            Intent intent = new Intent(Common.KEY_BARBER_LOAD_DONE);
                            intent.putParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE, (ArrayList<? extends Parcelable>) mListBarber);
                            mLocalBroadcastManager.sendBroadcast(intent);

                            mDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                }
            });

        }


    }

    // Broadcast Recevier
    private BroadcastReceiver enableBtnNextRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step = intent.getIntExtra(Common.KEY_STEP, 0);

            if (step == 1)
                Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if (step == 2)
                Common.currentBarber = intent.getParcelableExtra(Common.KEY_BARBER_SELECTED);
            else if (step == 3)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);

//            Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            mBtnNextStep.setEnabled(true);
            setColorStepButton();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        ButterKnife.bind(this);

        mDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(enableBtnNextRecevier,
                new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        setupStepView();
        setColorStepButton();

        mViewPagerSteps.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mViewPagerSteps.setOffscreenPageLimit(4);  // i have four fragment so i need keep state of this four page
        mViewPagerSteps.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                // Show step
                mStepView.go(position, true);

                if (position == 0){
                    mBtnPreviousStep.setEnabled(false);
                }else{
                    mBtnPreviousStep.setEnabled(true);
                }

                // Set disable button next here
                mBtnNextStep.setEnabled(false);
                setColorStepButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorStepButton() {

        if (mBtnNextStep.isEnabled()){
            mBtnNextStep.setBackgroundResource(R.drawable.btn_enable_bg);
        }else {
            mBtnNextStep.setBackgroundResource(R.drawable.btn_disable_bg);
        }

        if (mBtnPreviousStep.isEnabled()){
            mBtnPreviousStep.setBackgroundResource(R.drawable.btn_enable_bg);
        }else {
            mBtnPreviousStep.setBackgroundResource(R.drawable.btn_disable_bg);
        }
    }

    private void setupStepView() {

        List<String> mListStep = new ArrayList<>();
        mListStep.add("Salon");
        mListStep.add("Barber");
        mListStep.add("Time");
        mListStep.add("Confirm");
        mStepView.setSteps(mListStep);
    }

    @Override
    protected void onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(enableBtnNextRecevier);
        super.onDestroy();
    }

}
