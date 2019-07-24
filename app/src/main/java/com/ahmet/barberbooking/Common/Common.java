package com.ahmet.barberbooking.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import com.ahmet.barberbooking.HomeActivity;
import com.ahmet.barberbooking.Model.Barber;
import com.ahmet.barberbooking.Model.BookingInformation;
import com.ahmet.barberbooking.Model.Salon;
import com.ahmet.barberbooking.Model.TimeSlot;
import com.ahmet.barberbooking.Model.Token;
import com.ahmet.barberbooking.Model.User;
import com.ahmet.barberbooking.R;
import com.ahmet.barberbooking.Service.FCMService;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    // only user when need foemat key
    public static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");



    public static String convertTimeSoltToString(int solt) {

        switch (solt){

            case 0:
                return "9:00 - 9:30";
            case 1:
                return "9:30 - 10:00";
            case 2:
                return "10:00 - 10:30";
            case 3:
                return "10:30 - 11:00";
            case 4:
                return "11:00 - 11:30";
            case 5:
                return "11:30 - 12:00";
            case 6:
                return "12:00 - 12:30";
            case 7:
                return "12:30 - 13:00";
            case 8:
                return "13:00 - 13:30";
            case 9:
                return "13:30 - 14:00";
            case 10:
                return "14:00 - 14:30";
            case 11:
                return "14:30 - 15:00";
            case 12:
                return "15:00 - 15:30";
            case 13:
                return "15:30 - 16:00";
            case 14:
                return "16:00 - 16:30";
            case 15:
                return "16:30 - 17:00";
            case 16:
                return "17:00 - 17:30";
            case 17:
                return "17:30 - 18:00";
            case 18:
                return "18:00 - 18:30";
            case 19:
                return "18:30 - 19:00";
            default:
                return "Closed";
        }

    }

    public static String convertTimestampToKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        return simpleDateFormat.format(date);
    }

    public static String formatShoppingName(String name) {

        return name.length() > 13 ? new StringBuilder(name.substring(0, 10))
                                        .append(" ...").toString() : name;
    }

    public static void updateToken(Context mContext,String token) {

        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        if (accessToken != null){

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {

                    Token mToken = new Token();
                    mToken.setToken(token);
                    mToken.setUserPhone(account.getPhoneNumber().toString());
                    mToken.setTokenType(TOKEN_TYPE.CLIENT);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(account.getPhoneNumber().toString())
                            .set(mToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        } else {

            Paper.init(mContext);
            String user = Paper.book().read(Common.KEY_LOGGED);
            if (user != null){

                if (!TextUtils.isEmpty(user)){

                    Token mToken = new Token();
                    mToken.setToken(token);
                    mToken.setUserPhone(user);
                    mToken.setTokenType(TOKEN_TYPE.CLIENT);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(user)
                            .set(mToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL,
                    "SAJAHMET Barber Booking Client App", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Barber Booking Client App");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSound(sound)
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
                .document(city)
                .collection("Branch")
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


    public enum TOKEN_TYPE {

        CLIENT,
        BARBER,
        MANAGER
    }
}
