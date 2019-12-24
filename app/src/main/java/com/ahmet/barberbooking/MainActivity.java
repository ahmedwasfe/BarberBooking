package com.ahmet.barberbooking;

/**
 * Created by Android Studio.
 * User: ahmet
 * Date: 5/23/2019
 * Time: 11:47 AM
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Model.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int APP_REQUEST_CODE = 1881;
    //private static final int APP_REQUEST_CODE = 1882;

    private List<AuthUI.IdpConfig> mListProvidersPhone;
    private List<AuthUI.IdpConfig> mListProvidersFacebook;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private CallbackManager callbackManager;
//
    @BindView(R.id.txt_skip)
    TextView mLoginWithFacebook;
    @BindView(R.id.facebook_login)
    LoginButton facebookLogin;
//    @BindView(R.id.btn_login)
//    Button mBtnLogin;

    @OnClick(R.id.btn_mobile_login)
    void loginUser(){

       startActivityForResult(AuthUI.getInstance()
       .createSignInIntentBuilder()
       .setAvailableProviders(mListProvidersPhone).build(), APP_REQUEST_CODE);
    }

    @OnClick(R.id.txt_skip)
    void skipLogin(){

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(mListProvidersFacebook).build(), APP_REQUEST_CODE);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // printKeyHash();

        mListProvidersPhone = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
//        mListProvidersFacebook = Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null)
                checkUserFromFirebase(user);
        };


        Dexter.withActivity(this)
                .withPermissions(new String[]{
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.ACCESS_FINE_LOCATION
                }).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null){  // If already logged
                    // Get Token
                    getToken();

                    if (AccessToken.getCurrentAccessToken() != null){
                        getToken();

                    }

                }else {
                    setContentView(R.layout.activity_main);
                    ButterKnife.bind(MainActivity.this);

                    callbackManager = CallbackManager.Factory.create();
                    facebookLogin.setPermissions(Arrays.asList("email", "public_profile"));
                    facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {

                            if (loginResult != null){
                                getToken();
                            }
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {

                            Log.e("FACEBOOK_LOGIN_ERROR", error.getMessage());
                        }
                    });

                }


            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();



//        printKeyHash();
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken != null)
                loadUserDataFromFacebook(currentAccessToken);
        }
    };

    private void loadUserDataFromFacebook(AccessToken newAccessToken){

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, (object, response) -> {
            try {
                String firstName = object.getString("first_name");
                String last_name = object.getString("last_name");
                String email = object.getString("email");

                String id = object.getString("id");
                String imageUrl = "https://graph.facebook.com/"+id+"/picture?type=normal";

                String userData = firstName + "\n" + last_name + "\n" + email;
                String name = firstName + " " + last_name;


                Log.i("USER_DATA", userData);

                User user = new User(name, "", email);
                FirebaseFirestore.getInstance().collection("User")
                        .document(email)
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Addedd data successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "first_name,last_name,email");
        request.setParameters(bundle);
        request.executeAsync();


    }

    private void checkUserFromFirebase(FirebaseUser user) {

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        Common.updateToken(MainActivity.this, task.getResult().getToken());
                        Log.d("TOKEN", task.getResult().getToken());

                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra(Common.IS_LOGIN, true);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Error_Connection", e.getMessage());

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra(Common.IS_LOGIN, true);
                    startActivity(intent);
                    finish();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_REQUEST_CODE){

            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i("CURRENT_USER", user.getPhoneNumber());

            }else {

                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
                Log.e("FAILED_TO_SIGN_IN", idpResponse.getError().getMessage());

            }
        }
    }

    private void getToken(){

        // Get Token
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (task.isSuccessful()){

                            Common.updateToken(MainActivity.this, task.getResult().getToken());
                            Log.d("TOKEN", task.getResult().getToken());

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra(Common.IS_LOGIN, true);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra(Common.IS_LOGIN, true);
                        startActivity(intent);
                        finish();
                    }
                });

    }

    private void printKeyHash() {

        // K7Kw695YvlJLT253rS+7e+zRea8=

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES
            );
            for (Signature signature : packageInfo.signatures){
                MessageDigest digest = MessageDigest.getInstance("SHA");
                digest.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(digest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        super.onStop();
    }
}
