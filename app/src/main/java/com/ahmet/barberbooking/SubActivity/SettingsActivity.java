package com.ahmet.barberbooking.SubActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.MainActivity;
import com.ahmet.barberbooking.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingsActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    @BindView(R.id.input_update_name)
    EditText mInputName;
    @BindView(R.id.input_update_mobile)
    EditText mInputMobile;
    @BindView(R.id.input_update_email)
    EditText mInputEmail;
    @BindView(R.id.txt_app_version)
    TextView mAppVersion;

    @OnClick(R.id.txt_log_out)
    void btnLogOut(){

        logOut();
    }

    @OnClick(R.id.txt_clear_history)
    void clearHistory(){

        new AlertDialog.Builder(this)
                .setMessage("Are you sure want to clear history")
                .setCancelable(false)
                .setPositiveButton("Clear history", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                                .collection("User")
                                .document(Common.currentUser.getPhoneNumber())
                                .collection("Booking");
                        collectionReference.whereEqualTo("done", true);
                        collectionReference.document().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Toast.makeText(SettingsActivity.this, "Clear history Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        ;
                    }
                })
                .setCancelable(true)
                .show();

    }


    @BindView(R.id.img_edit_name)
    ImageView mEditName;
    @BindView(R.id.img_update_name)
    ImageView mUpdateName;

    @OnClick(R.id.img_edit_name)
    void btnEditName(){
        mInputName.setEnabled(true);
        mEditName.setVisibility(View.GONE);
        mUpdateName.setVisibility(View.VISIBLE);

    }
    @OnClick(R.id.img_update_name)
    void btnUpdateName(){
        updateName();

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mUnbinder = ButterKnife.bind(this);

        mInputName.setBackground(null);
        mInputEmail.setBackground(null);
        mInputMobile.setBackground(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        getAppVersion();
        loadUserInfo();


    }

    private void loadUserInfo(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){

            FirebaseFirestore.getInstance().collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot snapshot = task.getResult();
                            mInputName.setText(snapshot.getString("name"));
                            mInputMobile.setText(snapshot.getString("phoneNumber"));


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateName(){

        String name = mInputName.getText().toString();
        Map<String, Object> mapName = new HashMap<>();
        mapName.put("name", name);
        FirebaseFirestore.getInstance().collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .update(mapName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, "Update name successfully", Toast.LENGTH_SHORT).show();
                            mInputName.setEnabled(false);
                            mEditName.setVisibility(View.VISIBLE);
                            mUpdateName.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void getAppVersion(){
        try {
            PackageInfo packageInfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            String appVersion = packageInfo.versionName;
            mAppVersion.setText("App Version : " +  appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void logOut() {


        new AlertDialog.Builder(this)
                .setMessage("Are you sure want to Log Out")
                .setCancelable(false)
                .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AuthUI.getInstance()
                                .signOut(SettingsActivity.this)
                                .addOnCompleteListener(task -> {
                                    // user is now signed out
                                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                });

                    }
                })
                .setCancelable(true)
                .show();
    }

}
