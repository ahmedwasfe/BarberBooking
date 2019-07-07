package com.ahmet.barberbooking.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.SalonAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SpacesItemDecoration;
import com.ahmet.barberbooking.Interface.IBranchLoadListener;
import com.ahmet.barberbooking.Interface.ISalonLoadListener;
import com.ahmet.barberbooking.Model.Salon;
import com.ahmet.barberbooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingSalonFragment extends Fragment implements IBranchLoadListener,
                ISalonLoadListener {

    @BindView(R.id.materialSpinner)
    MaterialSpinner mSpinerBranch;
    @BindView(R.id.recycler_select_salon)
    RecyclerView mRecyclerSalon;

    private Unbinder mUnbinder;

    // Variable
    private CollectionReference mReferenceAllSalon;
    private CollectionReference mReferenceBranch;

    private IBranchLoadListener mIBranchLoadListener;
    private ISalonLoadListener mISalonLoadListener;

    private AlertDialog mDialog;

    static BookingSalonFragment instance;

    public static BookingSalonFragment getInstance(){
        if (instance == null){
            instance = new BookingSalonFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReferenceAllSalon = FirebaseFirestore.getInstance().collection("AllSalon");
        mReferenceBranch = FirebaseFirestore.getInstance().collection("Branch");

        mIBranchLoadListener = this;
        mISalonLoadListener = this;

        mDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_booking_salon, container, false);

        mUnbinder = ButterKnife.bind(this, layoutView);

        // init View
        initView();
        loadAllSalon();

        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void initView() {

        mRecyclerSalon.setHasFixedSize(true);
        mRecyclerSalon.setLayoutManager(new StaggeredGridLayoutManager(
                2, LinearLayout.VERTICAL));
        mRecyclerSalon.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadAllSalon() {

        mReferenceAllSalon.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<String> mList = new ArrayList<>();
                        mList.add("Please choose city");

                        for (QueryDocumentSnapshot snapshot : task.getResult()){
                            mList.add(snapshot.getId());
                        }
                        mIBranchLoadListener.onLoadAllSalonSuccess(mList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mIBranchLoadListener.onLoadAllSalonFailed(e.getMessage());
            }
        });

    }

    @Override
    public void onLoadAllSalonSuccess(List<String> mListBranch) {

        mSpinerBranch.setItems(mListBranch);

        mSpinerBranch.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0){
                    loadBranchOfCity(item.toString());
                }else {
                    mRecyclerSalon.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadBranchOfCity(String city) {

        mDialog.show();

        Common.city = city;

        mReferenceBranch = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(city)
                .collection("Branch");

        mReferenceBranch.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Salon> mListSalon = new ArrayList<>();

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                Salon salon = snapshot.toObject(Salon.class);
                                salon.setSalonID(snapshot.getId());
                                mListSalon.add(salon);
                            }
                            mISalonLoadListener.onLoadSalonSuccess(mListSalon);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mISalonLoadListener.onLoadSalonFailed(e.getMessage());
            }
        });

    }

    @Override
    public void onLoadAllSalonFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadSalonSuccess(List<Salon> mListSalon) {

        SalonAdapter mSalonAdapter = new SalonAdapter(getActivity(), mListSalon);
        mRecyclerSalon.setAdapter(mSalonAdapter);
        mRecyclerSalon.setVisibility(View.VISIBLE);

        mDialog.dismiss();
    }

    @Override
    public void onLoadSalonFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        mDialog.dismiss();
    }
}
