package com.ahmet.barberbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Fragments.HomeFragment;
import com.ahmet.barberbooking.Fragments.ShoppingFragment;
import com.ahmet.barberbooking.Model.User;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.btn_navigation_home)
    BottomNavigationView mNavigationView;


    private BottomSheetDialog mSheetDialog;
    private AlertDialog mDialog;

    @BindView(R.id.progress_spin_kit)
    ProgressBar mProgressBar;

    private CollectionReference mCollectionUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

//        Sprite rotatingPlane = new RotatingPlane();
//        mProgressBar.setIndeterminateDrawable(rotatingPlane);


        // init
        mCollectionUser = FirebaseFirestore.getInstance().collection("User");
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .build();

        // check intent , if is login = true enable full access
        // if is login = false , just let user around shoppingto view
        if (getIntent() != null){
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin){
                mDialog.show();
                mProgressBar.setVisibility(View.VISIBLE);
                // check if user is exists
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        if (account != null){

                            // Save user phone by Paper
                            Paper.init(HomeActivity.this);
                            Paper.book().write(Common.KEY_LOGGED, account.getPhoneNumber().toString());

                            DocumentReference mCurrentUser = mCollectionUser.document(account.getPhoneNumber().toString());
                            mCurrentUser.get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot userSnapshot = task.getResult();
                                                if (!userSnapshot.exists()){
                                                    showUpdateDialog(account.getPhoneNumber().toString());
                                                }else {
                                                    // If user alerady available in our system
                                                    Common.currentUser = userSnapshot.toObject(User.class);
                                                    mNavigationView.setSelectedItemId(R.id.nav_home);
                                                }
                                                if (mDialog.isShowing())
                                                    mDialog.dismiss();
                                                mProgressBar.setVisibility(View.GONE);

                                               // checkRatingDialog();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Toast.makeText(HomeActivity.this, ""+
                                accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }

        // btn Nav
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_home)
                    fragment = new HomeFragment();
                else if (menuItem.getItemId() == R.id.nav_shopping)
                    fragment = new ShoppingFragment();

                return loadFragmemt(fragment);
            }
        });

        //loadFragmemt(new HomeFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkRatingDialog();
    }

    private void checkRatingDialog() {

        Paper.init(this);
        String dataSerialized = Paper.book().read(Common.KEY_RATING_INFORMATION, "");
        // If not null
        if (!TextUtils.isEmpty(dataSerialized)){

            Map<String, String> mMapRecivedData = new Gson()
                    .fromJson(dataSerialized,
                            new TypeToken<Map<String, String>>(){}.getType());

            if (mMapRecivedData != null){

                Common.showRatingDialog(this,
                        mMapRecivedData.get(Common.KEY_RATING_CITY),
                        mMapRecivedData.get(Common.KEY_RATING_SALON_ID),
                        mMapRecivedData.get(Common.KEY_RATING_SALON_NAME),
                        mMapRecivedData.get(Common.KEY_RATING_BARBER_ID));
            }
        }
    }

    private boolean loadFragmemt(Fragment fragment) {
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout_home, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void showUpdateDialog(String phoneNumber) {


        // init dialog
        mSheetDialog = new BottomSheetDialog(this);
        mSheetDialog.setCancelable(false);
        mSheetDialog.setCanceledOnTouchOutside(false);

        View sheetView = getLayoutInflater().inflate(R.layout.layout_sheet_dialog, null);

        Button mBtnUpdate = sheetView.findViewById(R.id.btn_update);
        TextInputEditText mInputName = sheetView.findViewById(R.id.input_name);
        TextInputEditText mInputAddress = sheetView.findViewById(R.id.input_address);


        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mDialog.isShowing())
                    mDialog.show();
                mProgressBar.setVisibility(View.VISIBLE);

                String name = mInputName.getText().toString();
                String address = mInputAddress.getText().toString();

                User user = new User(name, address, phoneNumber);

                mCollectionUser.document(phoneNumber)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mSheetDialog.dismiss();
                                if (mDialog.isShowing())
                                    mDialog.dismiss();
                                mProgressBar.setVisibility(View.GONE);

                                Common.currentUser = user;
                                mNavigationView.setSelectedItemId(R.id.nav_home);

                                Toast.makeText(HomeActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mSheetDialog.dismiss();
                        if (mDialog.isShowing())
                            mDialog.dismiss();
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSheetDialog.setContentView(sheetView);
        mSheetDialog.show();
    }

}
