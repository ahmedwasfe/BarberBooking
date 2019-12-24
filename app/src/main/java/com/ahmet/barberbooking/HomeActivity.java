package com.ahmet.barberbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Databse.CartDatabase;
import com.ahmet.barberbooking.Databse.DatabaseUtils;
import com.ahmet.barberbooking.Fragments.CurrentBookingDialog;
import com.ahmet.barberbooking.Interface.IBookingInfoChangeListener;
import com.ahmet.barberbooking.Interface.IBookingInfoLoadListener;
import com.ahmet.barberbooking.Interface.ICountItemInCartListener;
import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.Model.Salon;
import com.ahmet.barberbooking.Model.User;
import com.ahmet.barberbooking.SubActivity.AllBookingActivity;
import com.ahmet.barberbooking.SubActivity.BookingActivity;
import com.ahmet.barberbooking.SubActivity.CartActivity;
import com.ahmet.barberbooking.SubActivity.SettingsActivity;
import com.ahmet.barberbooking.SubActivity.ShoppingActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.reflect.TypeToken;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements IBookingInfoLoadListener, IBookingInfoChangeListener,
        ICountItemInCartListener, OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Location mLastLocation;
    private PlacesClient mPlacesClient;

    private View mMapView;

    private final float DEFAULT_ZOOM = 18;

    private BottomSheetDialog mSheetDialog;

    // local database
    private CartDatabase mCartDatabase;

    private AlertDialog mDialog;

    @BindView(R.id.linear_user_info)
    LinearLayout mLinearUserInfo;
    @BindView(R.id.txt_user_name)
    TextView mTxtUserName;
    @BindView(R.id.txt_user_phone)
    TextView mTxtUserPhone;
    @BindView(R.id.notification_badget_cart)
    NotificationBadge mNotificationBadge;

    @OnClick(R.id.img_current_booking)
    void submitCurrentBooking() {
        // Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        CurrentBookingDialog bookingFragment = CurrentBookingDialog.getInstance();
        bookingFragment.show(getSupportFragmentManager(), "Current booking");
    }

    @BindView(R.id.card_booking_information)
    CardView mCardBookingInfo;
    @BindView(R.id.txt_address_salon)
    TextView mTxtAddressSalon;
    @BindView(R.id.txt_time)
    TextView mTxtTime;
    @BindView(R.id.txt_salon_barber)
    TextView mTxtSalonBarber;
    @BindView(R.id.txt_time_remain)
    TextView mTxtTimeRemain;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.tool_bar_home)
    Toolbar mToolbar;

    private ActionBarDrawerToggle mDrawerToggle;

    private TextView mTextUserName;
    CircleImageView mUserImage;

    private IBookingInfoLoadListener mIBookingInfoLoadListener;
    private IBookingInfoChangeListener mIBookingInfoChangeListener;

    private ListenerRegistration listenerUserBooking = null;
    EventListener<QuerySnapshot> eventUserBooking = null;

    private int CODE_EXCEPTION = 51;

    @OnClick(R.id.card_booking)
    void booking() {
        startActivity(new Intent(HomeActivity.this, BookingActivity.class));
    }

    @OnClick(R.id.btn_new_booking)
    void submitNewBooking() {
        startActivity(new Intent(HomeActivity.this, BookingActivity.class));
    }

    @OnClick(R.id.card_cart)
    void openCartActivity() {
        startActivity(new Intent(HomeActivity.this, CartActivity.class));
    }

    @OnClick(R.id.card_history)
    void openHistoryActivity() {
        startActivity(new Intent(HomeActivity.this, AllBookingActivity.class));
    }


    @OnClick(R.id.btn_delete_booking)
    void deleteBooking() {

        deleteBookingFromBarber(false);
    }

    @OnClick(R.id.btn_change_booking)
    void changeBooking() {

        changeBookingFromUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        ButterKnife.bind(this);

        // init Drawer and Navigation
        initNavigationDrawer();
        // init
        init();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(this, getString(R.string.google_api_key));
        mPlacesClient = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        showAllSalonOnMap();


        // check intent , if is login = true enable full access
        // if is login = false , just let salon_men around shoppingto view
        if (getIntent() != null) {
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {
                mDialog.show();
                // mProgressBar.setVisibility(View.VISIBLE);
                // check if salon_men is exists
                FirebaseUser userAccount = FirebaseAuth.getInstance().getCurrentUser();

                if (userAccount != null) {

                    // Save salon_men phone by Paper
                    Paper.init(HomeActivity.this);
                    Paper.book().write(Common.KEY_LOGGED, userAccount.getPhoneNumber());


                    FirebaseFirestore.getInstance().collection("User")
                            .document(userAccount.getPhoneNumber())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userSnapshot = task.getResult();
                                        if (!userSnapshot.exists()) {
                                            showUpdateDialog(userAccount.getPhoneNumber());

                                        } else {
                                            // If salon_men alerady available in our system
                                            Common.currentUser = userSnapshot.toObject(User.class);
                                            //mNavigationView.setSelectedItemId(R.id.nav_home);
                                            if (mDialog.isShowing())
                                                mDialog.dismiss();
                                            // check if Logged ?
                                            if (userAccount != null) {

                                                loadUserInfo();
                                                //  Need declare above loadUserBooking
                                                initRealtimeUserBooking();
                                                loadUserBooking();
                                                countItemsCart();

                                            }
                                        }
                                        if (mDialog.isShowing())
                                            mDialog.dismiss();

                                        // checkRatingDialog();
                                    }
                                }
                            });
                }
            }
            mDialog.dismiss();
        }


    }

    private void initNavigationDrawer() {

        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_booking:
                        startActivity(new Intent(HomeActivity.this, AllBookingActivity.class));
                        break;
                    case R.id.nav_notifications:
                        Toast.makeText(HomeActivity.this, "NOtifications", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_shopping:
                        startActivity(new Intent(HomeActivity.this, ShoppingActivity.class));
                        break;
                    case R.id.nav_cart:
                        startActivity(new Intent(HomeActivity.this, CartActivity.class));
                        break;
                    case R.id.nav_help:
                        Toast.makeText(HomeActivity.this, "Help", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                        break;

                }
                return true;
            }
        });

        View navigationLayout = mNavigationView.getHeaderView(0);
        mTextUserName = navigationLayout.findViewById(R.id.txt_user_name);
        mUserImage = navigationLayout.findViewById(R.id.img_user);


    }

    private void init() {

        mIBookingInfoLoadListener = this;
        mIBookingInfoChangeListener = this;

        // init cart database
        mCartDatabase = CartDatabase.getInstance(HomeActivity.this);

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait...")
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // loadFragment(new HomeFragment());
        checkRatingDialog();

        FirebaseUser userAccount = FirebaseAuth.getInstance().getCurrentUser();
        if (userAccount != null) {

            FirebaseFirestore.getInstance().collection("User")
                    .document(userAccount.getPhoneNumber())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    loadUserInfo();
//                              loadUserBooking();
                                    initRealtimeUserBooking();
                                }
                            }
                        }
                    });

            if (AccessToken.getCurrentAccessToken() != null){

                FirebaseFirestore.getInstance().collection("User")
                        .document(userAccount.getPhoneNumber())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot snapshot = task.getResult();
                                    if (snapshot.exists()) {
                                        loadUserInfo();
//                              loadUserBooking();
                                        initRealtimeUserBooking();
                                    }
                                }
                            }
                        });
            }
        }

    }


    private void loadUserDataFromFacebook(AccessToken newAccessToken){

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, (object, response) -> {
            try {
                String firstName = object.getString("first_name");
                String last_name = object.getString("last_name");
                String email = object.getString("email");
                String id = object.getString("id");
                String imageUrl = "https://graph.facebook.com/"+id+"/picture?type=normal";

                String userData = firstName + "\n" + last_name + "\n" + email;
                String name = firstName + "\n" + last_name;

                Log.i("USER_DATA", userData);
                User user = new User(name, "", email);
                FirebaseFirestore.getInstance().collection("User")
                        .document(email)
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(HomeActivity.this, "Addedd data successfully", Toast.LENGTH_SHORT).show();
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

    private void checkRatingDialog() {

        Paper.init(this);
        String dataSerialized = Paper.book().read(Common.KEY_RATING_INFORMATION, "");
        // If not null
        if (!TextUtils.isEmpty(dataSerialized)) {

            Map<String, String> mMapRecivedData = new Gson()
                    .fromJson(dataSerialized,
                            new TypeToken<Map<String, String>>() {
                            }.getType());

            if (mMapRecivedData != null) {

                Common.showRatingDialog(this,
                        mMapRecivedData.get(Common.KEY_RATING_CITY),
                        mMapRecivedData.get(Common.KEY_RATING_SALON_ID),
                        mMapRecivedData.get(Common.KEY_RATING_SALON_NAME),
                        mMapRecivedData.get(Common.KEY_RATING_BARBER_ID));
            }
        }
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

                String name = mInputName.getText().toString();
                String address = mInputAddress.getText().toString();

                User user = new User(name, address, phoneNumber);

                FirebaseFirestore.getInstance().collection("User")
                        .document(phoneNumber)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mSheetDialog.dismiss();
                                if (mDialog.isShowing())
                                    mDialog.dismiss();

                                Common.currentUser = user;

                                Toast.makeText(HomeActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mSheetDialog.dismiss();
                        if (mDialog.isShowing())
                            mDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSheetDialog.setContentView(sheetView);
        mSheetDialog.show();
    }

    private void changeBookingFromUser() {

        androidx.appcompat.app.AlertDialog.Builder mChangeDialog =
                new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this)
                        .setCancelable(false)
                        .setTitle(getString(R.string.message_welcome))
                        .setMessage(getString(R.string.message_change_booking))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //  True because we call we will button change
                                deleteBookingFromBarber(true);
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mChangeDialog.show();

    }

    private void deleteBookingFromBarber(boolean isChange) {

        /* To delete booking
         * First we need delete from Barber collections
         * Aftar that , We will delete from salon_men booking collections
         * And final , delete event
         *
         * We need load Common.currentBooking because we need some data from BookingInformation
         */

        if (Common.currentBooking != null) {

            mDialog.show();

            // Get booking information in barber object
            DocumentReference mBarberBookingInfoRef = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentBooking.getSalonID())
                    .collection("Barber")
                    .document(Common.currentBooking.getBarberID())
                    .collection(Common.convertTimestampToKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getTimeSlot().toString());

            // When we have document, just delete it
            mBarberBookingInfoRef
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            /*
                             *After delete on barber done
                             * We will start delete from User
                             */
                            deleteBookingFromUser(isChange);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            Toast.makeText(HomeActivity.this, "Current Booking must not be null", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBookingFromUser(boolean isChange) {

        // First , we need get information from salon_men object
        if (!TextUtils.isEmpty(Common.currentBookingId)) {

            DocumentReference mUserBookingInfoRef = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking")
                    .document(Common.currentBookingId);

            // Delete
            mUserBookingInfoRef
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            /*
                             *Aftar delete from salon_men , just delete from calendar
                             *First , we need get save uri of event we just add
                             */
                            Paper.init(HomeActivity.this);

                            if (Paper.book().read(Common.EVENT_URI_CACHE) != null) {

                                String event = Paper.book().read(Common.EVENT_URI_CACHE).toString();
                                Uri eventUri = null;

                                if (event != null && !TextUtils.isEmpty(event))
                                    eventUri = Uri.parse(event);

                                if (eventUri != null)
                                    HomeActivity.this.getContentResolver().delete(eventUri, null, null);
                            }


                            Toast.makeText(HomeActivity.this, "Success delete information booking ", Toast.LENGTH_SHORT).show();

                            //Refresh
                            loadUserBooking();

                            // Check id isChange -> call from change button , we have will fired interface
                            if (isChange)
                                mIBookingInfoChangeListener.onBookingInfoChange();

                            mDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", e.getMessage());
                }
            });

        } else {

            mDialog.dismiss();

            Toast.makeText(HomeActivity.this, "Booking information Id must not be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserBooking() {

        // /User/+970592435704/Booking/H4InjDGyf4NsN6TPENGH
        CollectionReference mUserBookingReference = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        // Get current data
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        // Select booking information from firebase database with done = false timestamp greater today
        mUserBookingReference.whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)  // Only take 1
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            if (!task.getResult().isEmpty()) {

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                    BookingInformation bookingInfo = documentSnapshot.toObject(BookingInformation.class);
                                    mIBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInfo, documentSnapshot.getId());
                                    // Exit loop as soon as
                                }

                            } else {

                                mIBookingInfoLoadListener.onBookingInfoLoadEmpty();
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mIBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
                    }
                });

        // Here, after userBooking has been assign data (collections)
        // we will make realtime listener here

        // If eventUserBooking alerdy init
        if (eventUserBooking != null) {

            // only add if listenerUserBooking == null
            if (listenerUserBooking == null) {

                // That mean we just add one time
                listenerUserBooking = mUserBookingReference
                        .addSnapshotListener(eventUserBooking);
            }
        }


    }

    private void loadUserInfo() {

        //    mLinearUserInfo.setVisibility(View.VISIBLE);
//        mTxtUserName.setText(Common.currentUser.getName());
        //      mTxtUserPhone.setText(Common.currentUser.getPhoneNumber());
//
        // mToolbar.setTitle(Common.currentUser.getName());
        mTextUserName.setText(Common.currentUser.getName());
    }

    private void initRealtimeUserBooking() {

        // We only init event if event is null
        if (eventUserBooking != null) {

            eventUserBooking = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    // In this event, when it fired, we will call loadUserBooking
                    // to reload all booking information
                    loadUserBooking();
                }
            };

        }
    }

    private void countItemsCart() {

        DatabaseUtils.countItemsInCart(mCartDatabase, this);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInfo, String bookingId) {

        Common.currentBooking = bookingInfo;
        Common.currentBookingId = bookingId;

        mTxtAddressSalon.setText(" " + bookingInfo.getSalonAddress());
        mTxtTime.setText(" " + bookingInfo.getTime());
        mTxtSalonBarber.setText(" " + bookingInfo.getBarberName());

        String timeRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInfo.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(), 0).toString();

        mTxtTimeRemain.setText(" " + timeRemain);

        mCardBookingInfo.setVisibility(View.VISIBLE);

        if (mDialog.isShowing())
            mDialog.dismiss();


    }

    @Override
    public void onBookingInfoLoadFailed(String error) {
        Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
        Log.d("ERROR", error);
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        mCardBookingInfo.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoChange() {
        startActivity(new Intent(HomeActivity.this, BookingActivity.class));
    }

    @Override
    public void onCountItemCartSuccess(int count) {
        mNotificationBadge.setText(String.valueOf(count));
    }

    @Override
    public void onDestroy() {
        if (listenerUserBooking != null)
            listenerUserBooking.remove();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getCurrentUserLocation();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(HomeActivity.this, CODE_EXCEPTION);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_EXCEPTION) {
            if (resultCode == RESULT_OK) {
                getCurrentUserLocation();
            }
        }
    }

    private void getCurrentUserLocation() {

        mFusedLocationProviderClient
                .getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {

                            mLastLocation = task.getResult();
                            if (mLastLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                        DEFAULT_ZOOM));
                            } else {
                                mLocationRequest = LocationRequest.create();
                                mLocationRequest.setInterval(10000);
                                mLocationRequest.setFastestInterval(5000);
                                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                mLocationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);

                                        if (locationResult == null)
                                            return;

                                        mLastLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                                DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

                                        showAllSalonOnMap();
                                    }
                                };

                                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void showAllSalonOnMap() {

        FirebaseFirestore.getInstance().collection("AllSalon")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {

                                Salon salon = snapshot.toObject(Salon.class);
                                LatLng latLng = new LatLng(salon.getLatitude(), salon.getLongitude());
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(salon.getName())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.salon_location)));

                                Log.i("Salon", salon.getLatitude() + "\n" +
                                        salon.getLongitude() + "\n" +
                                        salon.getName());

                            }
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}
