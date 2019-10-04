package com.ahmet.barberbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.HistoryAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.Model.EventBus.UserBookingLoadEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class HistoryActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    @BindView(R.id.recycler_history)
    RecyclerView mRecyclerHistory;
    @BindView(R.id.txt_history)
    TextView mTxtHistory;

    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");

        mUnbinder = ButterKnife.bind(this);

        init();
        initView();

        loadUserBookingInformation();
    }

    private void loadUserBookingInformation() {

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");
        collectionReference.whereEqualTo("done", true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EventBus.getDefault().post(new UserBookingLoadEvent(false, e.getMessage()));
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    List<BookingInformation> mListBookingInfo = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()){

                        BookingInformation bookingInformation = documentSnapshot.toObject(BookingInformation.class);
                        mListBookingInfo.add(bookingInformation);
                    }

                    EventBus.getDefault().post(new UserBookingLoadEvent(true, mListBookingInfo));
                }
            }
        });
    }

    private void initView() {

        mRecyclerHistory.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerHistory.setLayoutManager(layoutManager);
        mRecyclerHistory.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }

    private void init() {

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void displayData(UserBookingLoadEvent event){

        if (event.isIsloaded()){

            HistoryAdapter historyAdapter = new HistoryAdapter(this, event.getmListBookingInfo());
            mRecyclerHistory.setAdapter(historyAdapter);

            mTxtHistory.setText(new StringBuilder("HISTORY (")
                        .append(event.getmListBookingInfo().size())
                        .append(")"));

        } else {
            Toast.makeText(this, "" + event.getError(), Toast.LENGTH_SHORT).show();
        }


    }
}
