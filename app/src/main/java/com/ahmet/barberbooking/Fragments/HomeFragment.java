package com.ahmet.barberbooking.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.barberbooking.Adapter.HomeSliderAdapter;
import com.ahmet.barberbooking.Adapter.LookBookAdapter;
import com.ahmet.barberbooking.SubActivity.BookingActivity;
import com.ahmet.barberbooking.SubActivity.CartActivity;
import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Databse.CartDatabase;
import com.ahmet.barberbooking.Databse.DatabaseUtils;
import com.ahmet.barberbooking.SubActivity.AllBookingActivity;
import com.ahmet.barberbooking.Interface.IBannerLoadListener;
import com.ahmet.barberbooking.Interface.IBookingInfoChangeListener;
import com.ahmet.barberbooking.Interface.IBookingInfoLoadListener;
import com.ahmet.barberbooking.Interface.ICountItemInCartListener;
import com.ahmet.barberbooking.Interface.ILookBookLoadListener;
import com.ahmet.barberbooking.Model.Banner;
import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.R;
import com.ahmet.barberbooking.Service.LoadingImageService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ss.com.bannerslider.Slider;

public class HomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadListener,
        IBookingInfoLoadListener, IBookingInfoChangeListener, ICountItemInCartListener ,
        OnMapReadyCallback {

    private Unbinder mUnbinder;

    private GoogleMap mMap;

    // local database
    private CartDatabase mCartDatabase;

    private AlertDialog mDialog;

    @BindView(R.id.linear_user_info)
    LinearLayout mLinearUserInfo;
    @BindView(R.id.txt_user_name)
    TextView mTxtUserName;
    @BindView(R.id.txt_user_phone)
    TextView mTxtUserPhone;
    @BindView(R.id.slider_banner)
    Slider mSliderBanner;
    @BindView(R.id.recycler_look_book)
    RecyclerView mRecyclerLookBook;
    @BindView(R.id.notification_badget_cart)
    NotificationBadge mNotificationBadge;

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

    // FireStore
    private CollectionReference mReferenceBanner, mReferenceLookBook;

    // Interface
    private IBannerLoadListener mIBannerLoadListener;
    private ILookBookLoadListener mILookBookLoadListener;
    private IBookingInfoLoadListener mIBookingInfoLoadListener;
    private IBookingInfoChangeListener mIBookingInfoChangeListener;

    private ListenerRegistration listenerUserBooking = null;
    EventListener<QuerySnapshot> eventUserBooking = null;

    @OnClick(R.id.card_booking)
    void booking(){
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    @OnClick(R.id.card_cart)
    void openCartActivity(){
        startActivity(new Intent(getActivity(), CartActivity.class));
    }

    @OnClick(R.id.card_history)
    void openHistoryActivity(){
        startActivity(new Intent(getActivity(), AllBookingActivity.class));
    }




    @OnClick(R.id.btn_delete_booking)
    void deleteBooking(){

        deleteBookingFromBarber(false);
    }

    @OnClick(R.id.btn_change_booking)
    void changeBooking(){

        changeBookingFromUser();
    }

    private void changeBookingFromUser() {

        androidx.appcompat.app.AlertDialog.Builder mChangeDialog =
                new androidx.appcompat.app.AlertDialog.Builder(getActivity())
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
        
        if (Common.currentBooking != null){

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
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            Toast.makeText(getActivity(), "Current Booking must not be null", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBookingFromUser(boolean isChange) {

        // First , we need get information from salon_men object
        if (!TextUtils.isEmpty(Common.currentBookingId)){

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
                            Paper.init(getActivity());

                            if (Paper.book().read(Common.EVENT_URI_CACHE) != null){

                                String event = Paper.book().read(Common.EVENT_URI_CACHE).toString();
                                Uri eventUri = null;

                                if (event != null && !TextUtils.isEmpty(event))
                                    eventUri = Uri.parse(event);

                                if (eventUri != null)
                                    getActivity().getContentResolver().delete(eventUri,null,null);
                            }



                            Toast.makeText(getActivity(), "Success delete information booking ", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", e.getMessage());
                }
            });

        } else {

            mDialog.dismiss();

            Toast.makeText(getActivity(), "Booking information Id must not be empty", Toast.LENGTH_SHORT).show();
        }
    }


    public HomeFragment() {

        mReferenceBanner = FirebaseFirestore.getInstance().collection("Banner");
        mReferenceLookBook = FirebaseFirestore.getInstance().collection("LookBook");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIBannerLoadListener = this;
        mILookBookLoadListener = this;
        mIBookingInfoLoadListener = this;
        mIBookingInfoChangeListener = this;

        // init cart database
        mCartDatabase = CartDatabase.getInstance(getActivity());

        mDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage("Please wait...")
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadUserBooking();
        countItemsCart();
    }

    private void loadUserBooking() {

        // /User/+970592435704/Booking/H4InjDGyf4NsN6TPENGH
        CollectionReference mUserBookingReference = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        // Get current data
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
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

                        if (task.isSuccessful()){

                            if (!task.getResult().isEmpty()){

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){

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
        if (eventUserBooking != null){

            // only add if listenerUserBooking == null
            if (listenerUserBooking == null) {

                // That mean we just add one time
                listenerUserBooking = mUserBookingReference
                        .addSnapshotListener(eventUserBooking);
            }
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_home, container, false);

        mUnbinder = ButterKnife.bind(this, layoutView);

        // Init
        Slider.init(new LoadingImageService());

        return layoutView;
    }

    private void loadUserInfo() {

        mLinearUserInfo.setVisibility(View.VISIBLE);
        mTxtUserName.setText(Common.currentUser.getName());
        mTxtUserPhone.setText(Common.currentUser.getPhoneNumber());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // check if Logged ?
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            //if (salon_men.getName() != null){
                loadUserInfo();
               // loadBanner();
               // loadLookBook();
            //  Need declare above loadUserBooking
                initRealtimeUserBooking();
                loadUserBooking();
                countItemsCart();
            //}

        }

    }

    private void initRealtimeUserBooking() {

        // We only init event if event is null
        if (eventUserBooking != null){

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

    private void loadLookBook() {

        mReferenceLookBook.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> mListLookBook = new ArrayList<>();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                Banner banner = snapshot.toObject(Banner.class);
                                mListLookBook.add(banner);
                            }
                            mILookBookLoadListener.onLoadLookBookSuccess(mListLookBook);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mILookBookLoadListener.onLoadLookBookFailed(e.getMessage());
            }
        });

    }

    private void loadBanner() {
        mReferenceBanner.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> mListBanner = new ArrayList<>();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                Banner banner = snapshot.toObject(Banner.class);
                                mListBanner.add(banner);
                            }
                            mIBannerLoadListener.onLoadBannerSuccess(mListBanner);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mIBannerLoadListener.onLoadBannerFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onLoadBannerSuccess(List<Banner> mListBanner) {
        mSliderBanner.setAdapter(new HomeSliderAdapter(mListBanner));
        mSliderBanner.setInterval(5000);
        mSliderBanner.setLoopSlides(true);
        mSliderBanner.setAnimateIndicators(true);

    }

    @Override
    public void onLoadBannerFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadLookBookSuccess(List<Banner> mListLookBook) {
        mRecyclerLookBook.setHasFixedSize(true);
        mRecyclerLookBook.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));
        mRecyclerLookBook.setAdapter(new LookBookAdapter(getActivity(), mListLookBook));
    }

    @Override
    public void onLoadLookBookFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        Log.d("ERROR", error);
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        mCardBookingInfo.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoChange() {
        startActivity(new Intent(getActivity(), BookingActivity.class));
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
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(31.3986114, 34.3376548);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Gaza"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
