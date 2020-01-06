package com.ahmet.barberbooking.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ahmet.barberbooking.Fragments.HomeFragment;
import com.ahmet.barberbooking.HomeActivity;
import com.ahmet.barberbooking.Model.Barber;
import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.Model.Salon;
import com.ahmet.barberbooking.Model.TimeSlot;
import com.ahmet.barberbooking.Model.Token;
import com.ahmet.barberbooking.Model.User;
import com.ahmet.barberbooking.R;
import com.ahmet.barberbooking.Service.FCMService;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

public class Common {

    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_SALON_STORE = "SALON_SAVE";
    public static final String KEY_BARBER_LOAD_DONE = "BARBER _LOAD_DONE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_BARBER_SELECTED = "BARBER_SELECTED";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_LOGGED = "USER_LOGGED";
    public static final String KEY_RATING_INFORMATION = "RATING_INFORMATION";

    public static final String KEY_RATING_CITY = "RATING_CITY";
    public static final String KEY_RATING_SALON_ID = "RATING_SALON_ID";
    public static final String KEY_RATING_SALON_NAME = "RATING_SALON_NAME";
    public static final String KEY_RATING_BARBER_ID = "RATING_BARBER_ID";

    public static final String EVENT_URI_CACHE = "SAVE_URI_EVENT";

    public static final Object DISABLE_TAG = "DISABLE";

    // Public Tag Firebase Collections
    public static final String KEY_COLLECTION_USER = "User";
    public static final String KEY_COLLECTION_AllSALON = "AllSalon";
    public static final String KEY_COLLECTION_BARBER = "Barber";
    public static final String KEY_COLLECTION_BOOKING = "Booking";
    public static final String KEY_COLLECTION_PRODUCTS = "Products";
    public static final String KEY_COLLECTION_SHOPPING = "Shopping";
    public static final String KEY_COLLECTION_NOTIFICATIONS = "Notifications";
    public static final String KEY_COLLECTION_TOKENS = "Tokens";

    public static final String KEY_LANGUAGE = "Language";
    public static final String KEY_DARK_MODE = "NightMode";

    public static final String KEY_LANGUAGE_EN = "en";
    public static final String KEY_LANGUAGE_AR = "ar";
    public static final String KEY_LANGUAGE_TR = "tr";
    public static final String KEY_LANGUAGE_FR = "fr";

    public static User currentUser;
    public static Salon currentSalon;
    public static Barber currentBarber;
    public static BookingInformation currentBooking;

    public static String IS_LOGIN = "IsLogin";
    public static String city = "";
    public static String currentBookingId = "";

    public static final int TIME_SLOT_TOTAL = 20;
    public static int setp = 0; // init first setp is 0
    public static int currentTimeSlot = -1;

    public static Calendar bookingDate = Calendar.getInstance();
    // only salon_men when need foemat key
    public static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");




    public static String convertTimeSoltToString(Context mContext, int solt) {


        switch (solt){

            case 0:
                return mContext.getString(R.string.zero);
            case 1:
                return mContext.getString(R.string.one);
            case 2:
                return mContext.getString(R.string.tow);
            case 3:
                return mContext.getString(R.string.three);
            case 4:
                return mContext.getString(R.string.four);
            case 5:
                return mContext.getString(R.string.five);
            case 6:
                return mContext.getString(R.string.six);
            case 7:
                return mContext.getString(R.string.seven);
            case 8:
                return mContext.getString(R.string.eight);
            case 9:
                return mContext.getString(R.string.nine);
            case 10:
                return mContext.getString(R.string.ten);
            case 11:
                return mContext.getString(R.string.eleven);
            case 12:
                return mContext.getString(R.string.twelve);
            case 13:
                return mContext.getString(R.string.thirteen);
            case 14:
                return mContext.getString(R.string.fourteen);
            case 15:
                return mContext.getString(R.string.fifteen);
            case 16:
                return mContext.getString(R.string.sixteen);
            case 17:
                return mContext.getString(R.string.seventeen);
            case 18:
                return mContext.getString(R.string.eighteen);
            case 19:
                return mContext.getString(R.string.nineteen);
            default:
                return mContext.getString(R.string.closed);
        }

    }

    public static String convertTimestampToKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        return simpleDateFormat.format(date);
    }

    public static String formatName(String name) {

        return name.length() > 13 ? new StringBuilder(name.substring(0, 10))
                                        .append(" ...").toString() : name;
    }

    public static void updateToken(Context mContext, String token) {

        FirebaseUser userAccount = FirebaseAuth.getInstance().getCurrentUser();

        if (userAccount != null) {

            Token mToken = new Token();
            mToken.setToken(token);
            mToken.setTokenType(TOKEN_TYPE.CLIENT);
            mToken.setUserPhone(userAccount.getPhoneNumber());

            FirebaseFirestore.getInstance()
                    .collection("Tokens")
                    .document(userAccount.getPhoneNumber())
                    .set(mToken)
                    .addOnCompleteListener(task -> {

                    });
        } else {

            Paper.init(mContext);
            String user = Paper.book().read(Common.KEY_LOGGED);
            if (user != null) {

                if (!TextUtils.isEmpty(user)) {

                    Token mToken = new Token();
                    mToken.setToken(token);
                    mToken.setUserPhone(user);
                    mToken.setTokenType(TOKEN_TYPE.CLIENT);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(user)
                            .set(mToken)
                            .addOnCompleteListener(task -> {

                            });

                }
            }

        }

    }

    public static void showNotification(Context mContext, int notificationId, String title, String content, Intent intent) {

        PendingIntent pendingIntent = null;

        if (intent != null) {

            pendingIntent = PendingIntent.getActivity(mContext,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String NOTIFICATION_CHANNEL = "sajahmet_barber_booking_client_app_channel_01";
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL,
                    "SAJAHMET Barber Booking Client App", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Barber Booking Client App");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[] {0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
       // Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                //.setSound(sound)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(notificationId, notification);

    }

    public static void showRatingDialog(Context mContext, String city, String salonId, String salonName, String barberId) {

        // First we need get DocumentReferance of Barber
        DocumentReference mBarberRatingRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(salonId)
                .collection("Barber")
                .document(barberId);
        mBarberRatingRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    Barber barberRate = task.getResult().toObject(Barber.class);
                    barberRate.setBarberID(task.getResult().getId());

                    // Create view for layout dialog
                    View dialogView = LayoutInflater.from(mContext)
                            .inflate(R.layout.layout_dialog_rating_barber, null);

                    // init Widgets
                    TextView mTxtSalonName = dialogView.findViewById(R.id.txt_salon_name_dialog);
                    TextView mTxtBarberName = dialogView.findViewById(R.id.txt_barber_name_dialog);
                    AppCompatRatingBar mRatingBarber = dialogView.findViewById(R.id.rating_barber_dialog);

                    // Set Data
                    mTxtSalonName.setText(salonName);
                    mTxtBarberName.setText(barberRate.getName());

                    // Create Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setView(dialogView)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                     // Here we will update
                                    // rating information to firebase

                                    Double orginalRating = barberRate.getRating();
                                    Long ratingTimes = barberRate.getRatingTimes();
                                    float userRating = mRatingBarber.getRating();

                                    Double finalRating = (orginalRating + userRating);

                                    // update Rating
                                    Map<String, Object> mMapRating = new HashMap<>();
                                    mMapRating.put("rating", finalRating);
                                    mMapRating.put("ratingTimes", ++ratingTimes);

                                    mBarberRatingRef.update(mMapRating)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()){

                                                       Toast.makeText(mContext, "thank you for rating", Toast.LENGTH_SHORT).show();
                                                       // Remove key
                                                       Paper.init(mContext);
                                                       Paper.book().delete(KEY_RATING_INFORMATION);
                                                   }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }).setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // We just dismiss dialog
                                    dialogInterface.dismiss();
                                }
                            }).setNeutralButton("NEVER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // That mean no rating we will delete key
                                    Paper.init(mContext);
                                    Paper.book().delete(KEY_RATING_INFORMATION);
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    public static void setFragment(Fragment fragment, int id, FragmentManager fragmentManager) {

        fragmentManager.beginTransaction()
                .replace(id, fragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
    }


    public static void setLanguage(Context mContext, String languageToLoad){

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getResources().updateConfiguration(config,mContext.getResources().getDisplayMetrics());

    }

    public static void setLanguage(Context mContext){

        SaveSettings mSaveSettings = new SaveSettings(mContext);

        if (mSaveSettings.getLanguageState().equals(mContext.getString(R.string.english)))
            setLanguage(mContext, "en");
        else if (mSaveSettings.getLanguageState().equals(mContext.getString(R.string.arabic)))
            setLanguage(mContext, "ar");
        else if (mSaveSettings.getLanguageState().equals(mContext.getString(R.string.turkish)))
            setLanguage(mContext, "tr");
        else if (mSaveSettings.getLanguageState().equals(mContext.getString(R.string.french)))
            setLanguage(mContext,"fr");
    }


    public enum TOKEN_TYPE {

        CLIENT,
        BARBER,
        MANAGER
    }
}
