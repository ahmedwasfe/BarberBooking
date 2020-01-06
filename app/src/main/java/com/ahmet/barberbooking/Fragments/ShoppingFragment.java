package com.ahmet.barberbooking.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.SalonAdapter;
import com.ahmet.barberbooking.Adapter.ShoppingAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SaveSettings;
import com.ahmet.barberbooking.Common.SpacesItemDecoration;
import com.ahmet.barberbooking.Interface.ISalonLoadListener;
import com.ahmet.barberbooking.Interface.IShoppingLoadListener;
import com.ahmet.barberbooking.Model.Salon;
import com.ahmet.barberbooking.Model.Shopping;
import com.ahmet.barberbooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShoppingFragment extends Fragment implements IShoppingLoadListener, ISalonLoadListener {

    private Unbinder mUnbinder;

    // firestore
    private CollectionReference mShoppingReference;

    // interface
    private IShoppingLoadListener mIShoppingLoadListener;
    private ISalonLoadListener mISalonLoadListener;

    private SaveSettings mSaveSettings;

    @BindView(R.id.chip_group)
    ChipGroup mChipGroup;
    @BindView(R.id.chip_wax)
    Chip chip_wax;
    @BindView(R.id.chip_spray)
    Chip chip_spray;
    @BindView(R.id.chip_hair_care)
    Chip mChipHairCare;
    @BindView(R.id.chip_body_care)
    Chip mChipBodyCare;

    @OnClick(R.id.chip_wax)
    void waxChipClick(){
        setSelecedChip(chip_wax);
        loadShoppingItem("Wax");
    }

    @OnClick(R.id.chip_spray)
    void sprayChipClick(){
        setSelecedChip(chip_spray);
        loadShoppingItem("Spray");
    }


    @OnClick(R.id.chip_hair_care)
    void chipHairCare(){
        setSelecedChip(mChipHairCare);
        loadShoppingItem("Hair Care");
    }


    @OnClick(R.id.chip_body_care)
    void chipBodyCare(){
        setSelecedChip(mChipBodyCare);
        loadShoppingItem("Body Care");
    }

    @BindView(R.id.recycler_shopping_items)
    RecyclerView mRecyclerShopping;

    private void loadShoppingItem(String itemMenu) {

        mShoppingReference = FirebaseFirestore.getInstance()
                .collection("Shopping")
                .document(itemMenu)
                .collection("Items");

        // Get data for shopping
        mShoppingReference
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mIShoppingLoadListener.onShoppingLoadFailed(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            List<Shopping> mListShopping = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                Shopping shopping = documentSnapshot.toObject(Shopping.class);
                                // Remember add it if you dont want to get null referance
                                shopping.setId(documentSnapshot.getId());
                                mListShopping.add(shopping);
                            }
                            mIShoppingLoadListener.onShoppingLoadSuccess(mListShopping);
                        }
//                        else {
//                            Log.d("TASKEXCEPTION", task.getException().getMessage());
//                        }
                    }
                });
    }


    private void setSelecedChip(Chip chip) {

        // Set color
        for (int x = 0; x < mChipGroup.getChildCount(); x++){

            Chip mChipItem = (Chip) mChipGroup.getChildAt(x);
            // If not selected
            if (mChipItem.getId() != chip.getId()){
                mChipItem.setChipBackgroundColorResource(R.color.colorGray);
                mChipItem.setTextColor(getResources().getColor(R.color.colorBlack));

                // If selected
            } else {
                mChipItem.setChipBackgroundColorResource(R.color.colorPrimary);
                mChipItem.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }
    }

    private String salonId = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIShoppingLoadListener = this;
        mISalonLoadListener = this;
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

        View layoutView = inflater.inflate(R.layout.fragment_shopping, container, false);

        mUnbinder = ButterKnife.bind(this, layoutView);

        initRecyclerView();

        loadShoppingItem("Wax");
      //  loadAllSalon();
//        loadAllProducts();

        return layoutView;
    }

    private void initRecyclerView() {

        mRecyclerShopping.setHasFixedSize(true);
        mRecyclerShopping.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
        mRecyclerShopping.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onShoppingLoadSuccess(List<Shopping> mListShopping) {
        ShoppingAdapter mShoppingAdapter = new ShoppingAdapter(getActivity(), mListShopping);
        mRecyclerShopping.setAdapter(mShoppingAdapter);

    }

    @Override
    public void onShoppingLoadFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    private void loadAllSalon(){
        FirebaseFirestore.getInstance().collection("AllSalon")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<Salon> mListSalon = new ArrayList<>();
                            for (DocumentSnapshot snapshot : task.getResult()){
                                Salon salon = snapshot.toObject(Salon.class);
                                salon.setSalonID(snapshot.getId());
                                salonId = snapshot.getId();
                                Log.i("SALON_ID", snapshot.getId());
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

    private void loadAllProducts(){

        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(salonId)
                .collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<Shopping> mListShopping = new ArrayList<>();
                            for (DocumentSnapshot snapshot : task.getResult()){
                                Shopping shopping = snapshot.toObject(Shopping.class);
                                shopping.setId(snapshot.getId());
                                Log.i("PRODUCT_ID", snapshot.getId());
                                mListShopping.add(shopping);
                            }
                           // mIShoppingLoadListener.onShoppingLoadSuccess(mListShopping);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // mIShoppingLoadListener.onShoppingLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onLoadSalonSuccess(List<Salon> mListSalon) {
        SalonAdapter salonAdapter = new SalonAdapter(getActivity(), mListSalon);
        mRecyclerShopping.setAdapter(salonAdapter);
    }

    @Override
    public void onLoadSalonFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }
}
