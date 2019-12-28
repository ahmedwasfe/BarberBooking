package com.ahmet.barberbooking.SubActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.ShoppingAdapter;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Interface.ISalonLoadListener;
import com.ahmet.barberbooking.Interface.IShoppingLoadListener;
import com.ahmet.barberbooking.Model.Salon;
import com.ahmet.barberbooking.Model.Shopping;
import com.ahmet.barberbooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class ShoppingActivity extends AppCompatActivity implements
        IShoppingLoadListener, ISalonLoadListener {

    private Unbinder mUnbinder;

    @BindView(R.id.searchable_spinner)
    SearchableSpinner mSearchableSpinner;
    @BindView(R.id.recycler_shopping)
    RecyclerView mRecyclerShopping;

    private IShoppingLoadListener iShoppingLoadListener;
    private ISalonLoadListener iSalonLoadListener;
    private List<Salon> mListSalon;

    private boolean isFirstTimeClick = true;

    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        getSupportActionBar().setTitle(R.string.shopping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUnbinder = ButterKnife.bind(this);

        init();

        loadAllproductsFromShopping();

        loadAllSalon();

        mSearchableSpinner.setTitle(getString(R.string.please_select_salon));
        mSearchableSpinner.setKeepScreenOn(true);
        mSearchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Fixed first time click
                if (!isFirstTimeClick){

                    Salon salon = mListSalon.get(position);
                    Toast.makeText(ShoppingActivity.this, salon.getName(), Toast.LENGTH_SHORT).show();
                    loadAllProducts(mListSalon.get(position));

                }
                else {
                    isFirstTimeClick = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void loadAllProducts(Salon salon) {

        mDialog.show();

        FirebaseFirestore.getInstance().collection(Common.KEY_COLLECTION_AllSalon)
                .document(salon.getSalonID())
                .collection(Common.KEY_COLLECTION_Products)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            List<Shopping> mListShopping = new ArrayList<>();
                            for (DocumentSnapshot snapshot : task.getResult()){
                                Shopping shopping = snapshot.toObject(Shopping.class);
                                shopping.setId(snapshot.getId());
                                mListShopping.add(shopping);
                                Log.i("SHOOPING",shopping.getName());

                            }

                            iShoppingLoadListener.onShoppingLoadSuccess(mListShopping);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iShoppingLoadListener.onShoppingLoadFailed(e.getMessage());
            }
        });
    }

    private void init() {

        iShoppingLoadListener = this;
        iSalonLoadListener = this;

        mRecyclerShopping.setHasFixedSize(true);
        mRecyclerShopping.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
                .build();
    }

    private void loadAllSalon(){

        mDialog.show();

        FirebaseFirestore.getInstance().collection(Common.KEY_COLLECTION_AllSalon)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        List<Salon> mListSalon = new ArrayList<>();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            Salon salon = snapshot.toObject(Salon.class);
                            salon.setSalonID(snapshot.getId());
                            mListSalon.add(salon);
                        }

                        iSalonLoadListener.onLoadSalonSuccess(mListSalon);
                    }
                }).addOnFailureListener(e -> iSalonLoadListener.onLoadSalonFailed(e.getMessage()));
    }

    @Override
    public void onShoppingLoadSuccess(List<Shopping> mListShopping) {

        ShoppingAdapter shoppingAdapter = new ShoppingAdapter(ShoppingActivity.this, mListShopping);
        mRecyclerShopping.setAdapter(shoppingAdapter);
        mDialog.dismiss();

    }

    @Override
    public void onShoppingLoadFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        mDialog.dismiss();
    }

    @Override
    public void onLoadSalonSuccess(List<Salon> mListSalon) {

        this.mListSalon = mListSalon;

        // Get all Salon Names
        List<String> mListSalonName = new ArrayList<>();
        for (Salon salon : mListSalon)
            mListSalonName.add(salon.getName());

        // Create Adapter and set to Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mListSalonName);
        mSearchableSpinner.setAdapter(adapter);

        mDialog.dismiss();
    }

    @Override
    public void onLoadSalonFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        mDialog.dismiss();
    }

    private void loadAllproductsFromShopping(){

        FirebaseFirestore.getInstance().collection(Common.KEY_COLLECTION_Shopping)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        List<Shopping> mListShopping = new ArrayList<>();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            Shopping shopping = snapshot.toObject(Shopping.class);
                            shopping.setId(snapshot.getId());
                            mListShopping.add(shopping);
                        }

                        iShoppingLoadListener.onShoppingLoadSuccess(mListShopping);
                    }
                });
    }
}
