package com.ahmet.barberbooking.SubActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Common.SaveSettings;
import com.ahmet.barberbooking.HomeActivity;
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
    @BindView(R.id.txt_current_language)
    TextView mTxtCurrentLanguage;
    @BindView(R.id.img_edit_name)
    ImageView mEditName;
    @BindView(R.id.img_update_name)
    ImageView mUpdateName;

    @BindView(R.id.switch_dark_mode)
    Switch mSwitchDarkMode;

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

    @OnClick(R.id.txt_log_out)
    void btnLogOut(){

        logOut();
    }

    @OnClick(R.id.card_clear_history)
    void clearHistory(){

        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_history)
                .setMessage(R.string.are_sure_clear_history)
                .setCancelable(false)
                .setPositiveButton(R.string.clear_history, (dialogInterface, i) -> {

                    CollectionReference collectionReference = FirebaseFirestore.getInstance()
                            .collection(Common.KEY_COLLECTION_USER)
                            .document(Common.currentUser.getPhoneNumber())
                            .collection(Common.KEY_COLLECTION_BOOKING);
                    collectionReference.whereEqualTo("done", true);
                    collectionReference.document().delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(SettingsActivity.this, getString(R.string.clear_history_success), Toast.LENGTH_SHORT).show();
                                }
                            })
                    ;
                }).setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();

    }

    @OnClick(R.id.card_language)
    void changeLanguage(){

        showDialogToChangeLanguage();
    }

    private void showDialogToChangeLanguage() {

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(R.array.language_selected,
                        -1,
                        (dialog, position) -> {

                    if (position == 0){
                        Common.setLanguage(this, Common.KEY_LANGUAGE_EN);
                        mTxtCurrentLanguage.setText(mSaveSettings.getLanguageState());
                        mSaveSettings.setLanguageState(Common.KEY_LANGUAGE_EN);
                        dialog.dismiss();
                        restartApp();
                    }else if (position == 1){
                        Common.setLanguage(this, Common.KEY_LANGUAGE_AR);
                        mTxtCurrentLanguage.setText(mSaveSettings.getLanguageState());
                        mSaveSettings.setLanguageState(Common.KEY_LANGUAGE_AR);
                        dialog.dismiss();
                        restartApp();
                    }else if (position == 2){
                        Common.setLanguage(this, Common.KEY_LANGUAGE_TR);
                        mTxtCurrentLanguage.setText(mSaveSettings.getLanguageState());
                        mSaveSettings.setLanguageState(Common.KEY_LANGUAGE_TR);
                        dialog.dismiss();
                        restartApp();
                    }else if (position == 3){
                        Common.setLanguage(this, Common.KEY_LANGUAGE_FR);
                        mTxtCurrentLanguage.setText(mSaveSettings.getLanguageState());
                        mSaveSettings.setLanguageState(Common.KEY_LANGUAGE_FR);
                        dialog.dismiss();
                        restartApp();
                    }

                }).show();

//
//        Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_language);
//
//        RadioGroup mRadioGroupLanguage = dialog.findViewById(R.id.radio_language);
//
//        mRadioGroupLanguage.setOnCheckedChangeListener((group, checkedId) -> {
//
//            switch (checkedId){
//
//                case R.id.radio_english:
//                    Common.setLanguage(this, "en");
//                    mTxtCurrentLanguage.setText(mSaveSettings.getLanguageState());
//                    mSaveSettings.setLanguageState(getString(R.string.english));
//                    dialog.dismiss();
//                    restartApp();
//                    break;
//                case R.id.radio_arabic:
//                    Common.setLanguage(this, "ar");
//                    mTxtCurrentLanguage.setText(mSaveSettings.getLanguageState());
//                    mSaveSettings.setLanguageState(getString(R.string.arabic));
//                    dialog.dismiss();
//                    restartApp();
//                    break;
//                case R.id.radio_turkish:
//                    Common.setLanguage(this, "tr");
//                    mTxtCurrentLanguage.setText(mSaveSettings.getLanguageState());
//                    mSaveSettings.setLanguageState(getString(R.string.turkish));
//                    dialog.dismiss();
//                    restartApp();
//                    break;
//            }
//        });
//
//        dialog.show();
    }


    private SaveSettings mSaveSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        mSaveSettings = new SaveSettings(this);
        if (mSaveSettings.getNightModeState() == true )
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);

        if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_EN))
            Common.setLanguage(this, Common.KEY_LANGUAGE_EN);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_AR))
            Common.setLanguage(this, Common.KEY_LANGUAGE_AR);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_TR))
            Common.setLanguage(this, Common.KEY_LANGUAGE_TR);
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_FR))
            Common.setLanguage(this,Common.KEY_LANGUAGE_FR);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mUnbinder = ButterKnife.bind(this);

        if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_EN))
            mTxtCurrentLanguage.setText(getString(R.string.english));
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_AR))
            mTxtCurrentLanguage.setText(getString(R.string.arabic));
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_TR))
            mTxtCurrentLanguage.setText(getString(R.string.turkish));
        else if (mSaveSettings.getLanguageState().equals(Common.KEY_LANGUAGE_FR))
            mTxtCurrentLanguage.setText(getString(R.string.french));


        if (mSaveSettings.getNightModeState() == true)
            mSwitchDarkMode.setChecked(true);

        mSwitchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked){
                mSaveSettings.setNightModeState(true);
                restartApp();
            }else{
                mSaveSettings.setNightModeState(false);
                restartApp();
            }
        });

        mInputName.setBackground(null);
        mInputEmail.setBackground(null);
        mInputMobile.setBackground(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        getAppVersion();
        loadUserInfo();


    }

    private void loadUserInfo(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){

            FirebaseFirestore.getInstance().collection(Common.KEY_COLLECTION_USER)
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
        FirebaseFirestore.getInstance().collection(Common.KEY_COLLECTION_USER)
                .document(Common.currentUser.getPhoneNumber())
                .update(mapName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, getString(R.string.update_name_success), Toast.LENGTH_SHORT).show();
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
            mAppVersion.setText(getString(R.string.app_version) +  appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void logOut() {


        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_sure_logout))
                .setCancelable(false)
                .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
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
                }).setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    private void restartApp() {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
