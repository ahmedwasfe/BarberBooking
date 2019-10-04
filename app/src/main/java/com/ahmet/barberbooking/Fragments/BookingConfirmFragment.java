package com.ahmet.barberbooking.Fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.barberbooking.Common.Common;
import com.ahmet.barberbooking.Databse.CartDatabase;
import com.ahmet.barberbooking.Databse.CartItem;
import com.ahmet.barberbooking.Databse.DatabaseUtils;
import com.ahmet.barberbooking.Interface.ICartItemLoadListener;
import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.Model.EventBus.ConfirmBookingEvent;
import com.ahmet.barberbooking.Model.FCMResponse;
import com.ahmet.barberbooking.Model.FCMSendData;
import com.ahmet.barberbooking.Model.Notification;
import com.ahmet.barberbooking.Model.Token;
import com.ahmet.barberbooking.R;
import com.ahmet.barberbooking.Retrofit.IFCMService;
import com.ahmet.barberbooking.Retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BookingConfirmFragment extends Fragment implements ICartItemLoadListener {

    private SimpleDateFormat mSimpleDateFormat;

   // private LocalBroadcastManager mLocalBroadcastManager;

    private Unbinder mUnbinder;

    private AlertDialog mDialog;

    private IFCMService mIfcmService;

    private CompositeDisposable mCompositeDisposable;

    // init views
    @BindView(R.id.txt_time_confirm_booking)
    TextView mTxtTimeBooking;
    @BindView(R.id.txt_barber_name_confirm_booking)
    TextView mTxtBarberName;
    @BindView(R.id.txt_salon_name_confirm)
    TextView mTxtSalonName;
    @BindView(R.id.txt_salon_website_confirm_booking)
    TextView mTxtSalonWebsite;
    @BindView(R.id.txt_salon_phone_confirm_booking)
    TextView mTxtSalonPhone;
    @BindView(R.id.txt_salon_open_hours_confirm_booking)
    TextView mTxtSalonOpenHours;
    @BindView(R.id.txt_salon_address_confirm_booking)
    TextView mTxtSalonAddress;
    @BindView(R.id.img_barber_confirm)
    ImageView mImgBarber;
    @OnClick(R.id.txt_salon_website_confirm_booking)
    void openWebSite(){
        try {
            String webSiteUrl = Common.currentSalon.getWebsite();
            if (webSiteUrl.startsWith("https://") || webSiteUrl.startsWith("http://")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Common.currentSalon.getWebsite()));
                startActivity(intent);
            }else
                Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();

        }catch (ActivityNotFoundException e){
            Toast.makeText(getActivity(), "No application can handle this request."
                    + " Please install a web browser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    @OnClick(R.id.btn_confirm_booking)
    void confirmBooking(){

        mDialog.show();

        DatabaseUtils.getAllItemFromCart(CartDatabase.getInstance(getActivity()), this);


    }

    private void addToUserBooking(BookingInformation bookingInfo) {

        // First create new Collection
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

        // Check if exist document in this collection
        // If any document with field done = false
        mUserBookingReference.whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)  // Only take 1
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.getResult().isEmpty()){

                            // Set data
                            mUserBookingReference.document()
                                    .set(bookingInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            // Create Notification
                                            Notification notification = new Notification();
                                            notification.setUuid(UUID.randomUUID().toString());
                                            notification.setTitle("New Booking");
                                            notification.setContent("You have a new appoiment for customer hair care with " + Common.currentUser.getName());
                                            // We will onl filter notification with 'read' is false on Staff App
                                            notification.setRead(false);
                                            notification.setServerTimestamp(FieldValue.serverTimestamp());

                                            // Submit Notification to 'Notifications' collection of Barber
                                            FirebaseFirestore.getInstance()
                                                    .collection("AllSalon")
                                                    .document(Common.currentSalon.getSalonID())
                                                    .collection("Barber")
                                                    .document(Common.currentBarber.getBarberID())
                                                    .collection("Notifications") // If it not available , it will be crate automaticlly
                                                    .document(notification.getUuid()) // Create Uniqe key
                                                    .set(notification)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            // First get Token base on Barber
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Tokens")
                                                                    .whereEqualTo("userPhone", Common.currentBarber.getUsername())
                                                                    .limit(1)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            if (task.isSuccessful() && task.getResult().size() > 0){

                                                                                Token token = new Token();
                                                                                for (DocumentSnapshot documentSnapshot : task.getResult())
                                                                                    token = documentSnapshot.toObject(Token.class);

                                                                                // Create data to send
                                                                                FCMSendData sendRequest = new FCMSendData();

                                                                                Map<String, String> mMapSendData = new HashMap<>();
                                                                                mMapSendData.put(Common.KEY_TITLE, "New Booking");
                                                                                mMapSendData.put(Common.KEY_CONTENT, "You have a new Booking from " + Common.currentUser.getName());

                                                                                sendRequest.setTo(token.getToken());
                                                                                sendRequest.setmMapData(mMapSendData);

                                                                                mCompositeDisposable.add(

                                                                                mIfcmService.sendNOtification(sendRequest)
                                                                                        .subscribeOn(Schedulers.io())
                                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                                        .subscribe(new Consumer<FCMResponse>() {
                                                                                            @Override
                                                                                            public void accept(FCMResponse fcmResponse) throws Exception {

                                                                                                mDialog.dismiss();

                                                                                                addToCalendar(Common.bookingDate,
                                                                                                        Common.convertTimeSoltToString(Common.currentTimeSlot));

                                                                                                resetStaticData();
                                                                                                getActivity().finish();;
                                                                                                Toast.makeText(getActivity(), "SuccessFully !", Toast.LENGTH_SHORT).show();

                                                                                            }
                                                                                        }, new Consumer<Throwable>() {
                                                                                            @Override
                                                                                            public void accept(Throwable throwable) throws Exception {
                                                                                                Log.d("NOTIFICATION_ERROR", throwable.getMessage());

                                                                                                addToCalendar(Common.bookingDate,
                                                                                                        Common.convertTimeSoltToString(Common.currentTimeSlot));

                                                                                                resetStaticData();
                                                                                                getActivity().finish();;
                                                                                                Toast.makeText(getActivity(), "SuccessFully !", Toast.LENGTH_SHORT).show();

                                                                                            }
                                                                                        }));
                                                                            }
                                                                        }
                                                                    });

                                                        }
                                                    });




                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (mDialog.isShowing())
                                        mDialog.dismiss();
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {

                            if (mDialog.isShowing())
                                mDialog.dismiss();

                            resetStaticData();
                            getActivity().finish();;
                            Toast.makeText(getActivity(), "SuccessFully !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addToCalendar(Calendar bookingDate, String startDate) {

        // Process Timestamp
        // I will use Timestamp to filter all booking with date is greater today
        // For only display all future booking
        String startTime  = Common.convertTimeSoltToString(Common.currentTimeSlot);
        // Split ex : 9:00 - 10:00
        String[] convertTime = startTime.split("-");
        // Get start time get : 9:00
        String[] startTimeConvert = convertTime[0].split(":");
        // I get 9
        int startHour = Integer.parseInt(startTimeConvert[0].trim());
        // I get 00
        int startMinute = Integer.parseInt(startTimeConvert[1].trim());

        String[] endTimeConvert = convertTime[1].split(":");
        // I get 10
        int endHour = Integer.parseInt(endTimeConvert[0].trim());
        // I get 00
        int endMinute = Integer.parseInt(endTimeConvert[1].trim());

        Calendar startEvent = Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        // Set event Start hour
        startEvent.set(Calendar.HOUR_OF_DAY, startHour);
        // Set event Start minute
        startEvent.set(Calendar.MINUTE, startMinute);

        Calendar endEvent = Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        // Set event End hour
        endEvent.set(Calendar.HOUR_OF_DAY, endHour);
        // Set event End minute
        endEvent.set(Calendar.MINUTE, endMinute);

        // After that i have startEvent and end Event, convertit to format String
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String startEventTime = calendarDateFormat.format(startEvent.getTime());
        String endEventTime = calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime, endEventTime, "Hair Cut Booking",
                            new StringBuilder("Hair Cut from")
                                .append(startTime)
                                .append(" with ")
                                .append(Common.currentBarber.getName())
                                .append(" at ")
                                .append(Common.currentSalon.getName()).toString(),
                            new StringBuilder("Address: ")
                                .append(Common.currentSalon.getAddress()).toString());

    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String descroption, String address) {

        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try {
           Date startDate = calendarDateFormat.parse(startEventTime);
           Date endDate = calendarDateFormat.parse(endEventTime);

            ContentValues contentEvent = new ContentValues();
            contentEvent.put(CalendarContract.Events.CALENDAR_ID, getCalendar(getActivity()));
            contentEvent.put(CalendarContract.Events.TITLE, title);
            contentEvent.put(CalendarContract.Events.DESCRIPTION, descroption);
            contentEvent.put(CalendarContract.Events.EVENT_LOCATION, address);

            // Time
            contentEvent.put(CalendarContract.Events.DTSTART, startDate.getTime());
            contentEvent.put(CalendarContract.Events.DTEND, endDate.getTime());
            contentEvent.put(CalendarContract.Events.ALL_DAY, 0);
            contentEvent.put(CalendarContract.Events.HAS_ALARM, 1);

            String timeZone = TimeZone.getDefault().getID();
            contentEvent.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

            Uri calendaersUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                calendaersUri = Uri.parse("content://com.android.calendar/events");
            else
                calendaersUri = Uri.parse("content://com.android.calendar/events");

            // Add to Content Resolver
            Uri saveUriOfCalendaer = getActivity().getContentResolver().insert(calendaersUri, contentEvent);

            // Save to cache
            Paper.init(getActivity());
            Paper.book().write(Common.EVENT_URI_CACHE, saveUriOfCalendaer.toString());
        } catch (ParseException e) {

        }
    }

    private String getCalendar(Context mContext) {

        // Get default Calendar ID of calendar of Gmail
        String gmailIDCalendar = "";
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendaersUri = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = mContext.getContentResolver();
        // Select all Calendar
        Cursor managedCursor = contentResolver.query(calendaersUri, projection, null, null, null);

        if (managedCursor.moveToFirst()){

            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);

            do {
                calName = managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com")){
                    gmailIDCalendar = managedCursor.getString(idCol);
                    // Exit as soon as haave id
                }
            }while (managedCursor.moveToNext());

            managedCursor.close();
        }

        return gmailIDCalendar;
    }

    private void resetStaticData() {

        Common.setp = 0;
        Common.currentSalon = null;
        Common.currentBarber = null;
        Common.currentTimeSlot = -1;
        // Current date added
        Common.bookingDate.add(Calendar.DATE, 0);
    }

/*
    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            loadDateBooking();
        }
    };
*/
    // ---------------------------------------------------------------------
    // Start Event Bus

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
    public void setDataBooking(ConfirmBookingEvent event){
        if (event.isConfirm())
            loadDateBooking();
    }

    // ---------------------------------------------------------------------


    private void loadDateBooking() {

        mTxtBarberName.setText(Common.currentBarber.getName());
        mTxtTimeBooking.setText(new StringBuilder(Common.convertTimeSoltToString(Common.currentTimeSlot))
                                .append(" at ")
                                .append(mSimpleDateFormat.format(Common.bookingDate.getTime())));
        mTxtSalonName.setText(Common.currentSalon.getName());
        mTxtSalonAddress.setText(Common.currentSalon.getAddress());
        mTxtSalonWebsite.setText(Common.currentSalon.getWebsite());
        mTxtSalonPhone.setText(Common.currentSalon.getPhone());
        mTxtSalonOpenHours.setText(Common.currentSalon.getOpenHour());
        mTxtSalonWebsite.setText(Common.currentSalon.getWebsite());

        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(Common.currentSalon.getSalonID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){

                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.get("salonType").equals("Men"))
                                Picasso.get().load(R.drawable.salon_men).into(mImgBarber);
                            else if (snapshot.get("salonType").equals("Women"))
                                Picasso.get().load(R.drawable.salon_women).into(mImgBarber);
                            else
                                Picasso.get().load(R.drawable.salon_men).into(mImgBarber);
                        }
                    }
                });
    }

    static BookingConfirmFragment instance;

    public static BookingConfirmFragment getInstance(){
        if (instance == null){
            instance = new BookingConfirmFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIfcmService = RetrofitClient.getInstance().create(IFCMService.class);

        mCompositeDisposable = new CompositeDisposable();

        // Apply format for date display on confirm
        mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        /*
          * mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
          * mLocalBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM_BOOKING));
        */

        mDialog = new SpotsDialog
                .Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_booking_confirm, container, false);

        mUnbinder = ButterKnife.bind(this, layoutView);

        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
       // mLocalBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        mCompositeDisposable.clear();;
        super.onDestroy();
    }

    @Override
    public void onLoadAllItemInCartSuccess(List<CartItem> mListCartItem) {

        // Here we will have all item on cart

        // Process Timestamp
        // I will use Timestamp to filter all booking with date is greater today
        // For only display all future booking
        String startTime  = Common.convertTimeSoltToString(Common.currentTimeSlot);
        // Split ex : 9:00 - 10:00
        String[] convertTime = startTime.split("-");
        // Get start time get : 9:00
        String[] startTimeConvert = convertTime[0].split(":");
        // I get 9
        int startHour = Integer.parseInt(startTimeConvert[0].trim());
        // I get 00
        int startMinute = Integer.parseInt(startTimeConvert[1].trim());

        Calendar bookingDateWithHourHouse = Calendar.getInstance();
        bookingDateWithHourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
        bookingDateWithHourHouse.set(Calendar.HOUR_OF_DAY, startHour);
        bookingDateWithHourHouse.set(Calendar.MINUTE, startMinute);

        // Create Timestamp object and apply to booking information
        Timestamp timestamp = new Timestamp(bookingDateWithHourHouse.getTime());

        // Create booking information
        BookingInformation bookingInfo = new BookingInformation();

       // bookingInfo.setCityBooking(Common.city);
        bookingInfo.setTimestamp(timestamp);
        // Always FALSE, because i will use this fieldto filter for display on salon_men
        bookingInfo.setDone(false);

        bookingInfo.setBarberID(Common.currentBarber.getBarberID());
        bookingInfo.setBarberName(Common.currentBarber.getName());

        bookingInfo.setCustomerName(Common.currentUser.getName());
        bookingInfo.setCustomerPhone(Common.currentUser.getPhoneNumber());

        bookingInfo.setSalonID(Common.currentSalon.getSalonID());
        bookingInfo.setSalonAddress(Common.currentSalon.getAddress());
        bookingInfo.setSalonName(Common.currentSalon.getName());

        bookingInfo.setTime(new StringBuilder(Common.convertTimeSoltToString(Common.currentTimeSlot))
                .append(" at ")
                .append(mSimpleDateFormat.format(bookingDateWithHourHouse.getTime())).toString());

        bookingInfo.setTimeSlot(Long.valueOf(Common.currentTimeSlot));

        bookingInfo.setmListCart(mListCartItem);

        // submit to barbber document
        DocumentReference mBookingReference =  FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.currentSalon.getSalonID())
                .collection("Barber")
                .document(Common.currentBarber.getBarberID())
                .collection(Common.mSimpleDateFormat.format(Common.bookingDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));
        // Write information
        mBookingReference.set(bookingInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Here i can write an function to check
                        // If already exist an booking , i will prevent new booking
                        // aftert add success booking information just clear cart
                        DatabaseUtils.clearCart(CartDatabase.getInstance(getActivity()));
                        addToUserBooking(bookingInfo);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""  + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
