package com.ahmet.barberbooking.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.barberbooking.Adapter.HistoryAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SaveSettings;
import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.Model.EventBus.UserBookingLoadEvent;
import com.ahmet.barberbooking.R;
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

public class HistoryFragment extends Fragment {

    Unbinder mUnbinder;

    @BindView(R.id.recycler_history)
    RecyclerView mRecyclerHistory;

    private AlertDialog mDialog;

    private SaveSettings mSaveSettings;

    private static HistoryFragment instance;
    public static HistoryFragment getInstance(){
        if (instance == null)
            instance = new HistoryFragment();
        return instance;
    }

    private void initView() {

        mRecyclerHistory.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerHistory.setLayoutManager(layoutManager);
        mRecyclerHistory.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
    }

    private void init() {

        mDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mSaveSettings = new SaveSettings(getActivity());

        if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_EN))
            Common.setLanguage(getActivity(), Common.KEY_LANGUAGE_EN);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_AR))
            Common.setLanguage(getActivity(), Common.KEY_LANGUAGE_AR);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_TR))
            Common.setLanguage(getActivity(), Common.KEY_LANGUAGE_TR);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_FR))
            Common.setLanguage(getActivity(),Common.KEY_LANGUAGE_FR);

        if (mSaveSettings.getNightModeState() == true)
            getActivity().setTheme(R.style.DarkTheme);
        else
            getActivity().setTheme(R.style.AppTheme);

        View layoutView = inflater.inflate(R.layout.fragment_history, container, false);

        mUnbinder = ButterKnife.bind(this, layoutView);

        init();
        initView();

        loadUserBookingInformation();

        return layoutView;
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
    public void displayData(UserBookingLoadEvent event){

        if (event.isIsloaded()){

            HistoryAdapter historyAdapter = new HistoryAdapter(getActivity(), event.getmListBookingInfo());
            mRecyclerHistory.setAdapter(historyAdapter);

//            mTxtHistory.setText(new StringBuilder("HISTORY (")
//                    .append(event.getmListBookingInfo().size())
//                    .append(")"));

        } else {
            Toast.makeText(getActivity(), "" + event.getError(), Toast.LENGTH_SHORT).show();
        }


    }


}
